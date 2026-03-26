package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerLoginAndAccessProtectedEndpoint() throws Exception {
        String registerBody = """
                {
                  "fullName": "Rudresh Sharma",
                  "email": "rudresh@example.com",
                  "password": "Password@123",
                  "mobileNumber": "9876543210"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("rudresh@example.com"));

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "rudresh@example.com",
                                  "password": "Password@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = loginResponse.split("\"token\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rudresh@example.com"));

        mockMvc.perform(post("/api/v1/quantities/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "thisQuantityDTO": {
                                    "value": 1.0,
                                    "unit": "FEET",
                                    "measurementType": "LengthUnit"
                                  },
                                  "thatQuantityDTO": {
                                    "value": 12.0,
                                    "unit": "INCHES",
                                    "measurementType": "LengthUnit"
                                  }
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/quantities/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "thisQuantityDTO": {
                                    "value": 1.0,
                                    "unit": "FEET",
                                    "measurementType": "LengthUnit"
                                  },
                                  "thatQuantityDTO": {
                                    "value": 12.0,
                                    "unit": "INCHES",
                                    "measurementType": "LengthUnit"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(2.0));
    }

    @Test
    void oauthSuccessReturnsJsonTokenPayload() throws Exception {
        String registerBody = """
                {
                  "fullName": "Rudresh Sharma",
                  "email": "rudresh@example.com",
                  "password": "Password@123",
                  "mobileNumber": "9876543210"
                }
                """;

        String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = registerResponse.split("\"token\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/oauth-success")
                        .param("token", token)
                        .param("email", "rudresh@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").value(10800))
                .andExpect(jsonPath("$.user.email").value("rudresh@example.com"));
    }

    @Test
    void oauthSuccessReturnsJsonErrorWhenTokenOrEmailIsMissing() throws Exception {
        mockMvc.perform(get("/oauth-success"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Missing token or email in OAuth success redirect"))
                .andExpect(jsonPath("$.path").value("/oauth-success"));
    }

    @Test
    void swaggerUiHtmlIsAccessibleWithoutRedirectingToGoogleLogin() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/swagger-ui/")));
    }
}
