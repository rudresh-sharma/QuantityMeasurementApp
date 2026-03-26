package com.app.quantitymeasurementapp.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private long expiresInSeconds;
    private UserProfileResponse user;
}
