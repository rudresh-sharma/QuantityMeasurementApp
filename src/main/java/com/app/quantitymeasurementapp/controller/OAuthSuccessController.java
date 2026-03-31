package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.dto.auth.AuthResponse;
import com.app.quantitymeasurementapp.dto.auth.UserProfileResponse;
import com.app.quantitymeasurementapp.security.JwtService;
import com.app.quantitymeasurementapp.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OAuthSuccessController {

    private final AuthService authService;
    private final JwtService jwtService;

    public OAuthSuccessController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "/oauth-success", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthSuccess(@RequestParam(required = false) String token,
                                          @RequestParam(required = false) String email) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Missing token or email in OAuth success redirect",
                    "path", "/oauth-success"
            ));
        }

        UserProfileResponse user = authService.getCurrentUser(email);
        AuthResponse authResponse = new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user
        );

        return ResponseEntity.ok(authResponse);
    }
}
