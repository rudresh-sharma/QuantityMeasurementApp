package com.app.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleUserSyncRequest {
    private String fullName;
    @NotBlank
    @Email
    private String email;
}
