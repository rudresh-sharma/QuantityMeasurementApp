package com.app.userservice.dto;

import com.app.userservice.model.AuthProvider;
import com.app.userservice.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    private String passwordHash;
    private String mobileNumber;
    @NotNull
    private UserRole role;
    @NotNull
    private AuthProvider authProvider;
    private boolean enabled;
}
