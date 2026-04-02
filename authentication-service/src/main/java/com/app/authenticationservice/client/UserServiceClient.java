package com.app.authenticationservice.client;

import com.app.authenticationservice.client.dto.CreateUserRequest;
import com.app.authenticationservice.client.dto.GoogleUserSyncRequest;
import com.app.authenticationservice.client.dto.UserRecordResponse;
import com.app.authenticationservice.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${services.user-service.base-url}") String userServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    public UserRecordResponse createUser(CreateUserRequest request) {
        try {
            return restClient.post()
                    .uri("/internal/users")
                    .body(request)
                    .retrieve()
                    .body(UserRecordResponse.class);
        } catch (RestClientResponseException ex) {
            throw mapException(ex);
        }
    }

    public UserRecordResponse getUserByEmail(String email) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/internal/users/by-email")
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .body(UserRecordResponse.class);
        } catch (RestClientResponseException ex) {
            throw mapException(ex);
        }
    }

    public UserRecordResponse syncGoogleUser(GoogleUserSyncRequest request) {
        try {
            return restClient.put()
                    .uri("/internal/users/google-sync")
                    .body(request)
                    .retrieve()
                    .body(UserRecordResponse.class);
        } catch (RestClientResponseException ex) {
            throw mapException(ex);
        }
    }

    private RuntimeException mapException(RestClientResponseException ex) {
        if (ex.getStatusCode() == HttpStatus.CONFLICT) {
            return new AuthException("Email already exists. Please login with your existing account.");
        }
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new AuthException("User not found");
        }
        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new AuthException("Invalid email or password");
        }
        return new AuthException("User service call failed: " + ex.getStatusText());
    }
}
