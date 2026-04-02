package com.app.userservice.dto;

import com.app.userservice.model.AuthProvider;
import com.app.userservice.model.UserEntity;
import com.app.userservice.model.UserRole;
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

    public static UserProfileResponse fromEntity(UserEntity user) {
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
