package com.app.authenticationservice.security;

import com.app.authenticationservice.client.dto.UserRecordResponse;
import com.app.authenticationservice.exception.AuthException;
import com.app.authenticationservice.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtService jwtService;
    private final String redirectUri;

    public GoogleOAuth2SuccessHandler(AuthService authService,
                                      JwtService jwtService,
                                      @Value("${auth.oauth2.redirect-uri:http://localhost:3000/oauth-success}") String redirectUri) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Google account email is required");
            return;
        }

        try {
            UserRecordResponse user = authService.processGoogleUser(name, email);
            String token = jwtService.generateToken(user);

            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", token)
                    .queryParam("email", user.getEmail())
                    .build()
                    .toUriString();

            response.sendRedirect(targetUrl);
        } catch (AuthException ex) {
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", ex.getMessage())
                    .build()
                    .toUriString();
            response.sendRedirect(targetUrl);
        }
    }
}
