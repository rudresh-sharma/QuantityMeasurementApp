package com.app.authenticationservice.client.dto;

import lombok.Data;

@Data
public class GoogleUserSyncRequest {
    private String fullName;
    private String email;
}
