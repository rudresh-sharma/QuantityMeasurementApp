package com.app.authenticationservice.dto.auth;

import com.app.authenticationservice.model.AuthProvider;
import com.app.authenticationservice.model.UserRole;
import com.app.authenticationservice.client.dto.UserRecordResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private UserRole role;
    private AuthProvider authProvider;

    public static UserProfileResponse fromUserRecord(UserRecordResponse user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole(),
                user.getAuthProvider()
        );
    }
}
