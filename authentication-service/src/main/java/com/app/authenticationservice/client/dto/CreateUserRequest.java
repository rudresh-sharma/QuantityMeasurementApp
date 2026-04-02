package com.app.authenticationservice.client.dto;

import com.app.authenticationservice.model.AuthProvider;
import com.app.authenticationservice.model.UserRole;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String fullName;
    private String email;
    private String passwordHash;
    private String mobileNumber;
    private UserRole role;
    private AuthProvider authProvider;
    private boolean enabled;
}
