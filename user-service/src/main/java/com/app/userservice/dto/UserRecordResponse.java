package com.app.userservice.dto;

import com.app.userservice.model.AuthProvider;
import com.app.userservice.model.UserEntity;
import com.app.userservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRecordResponse {
    private Long id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String mobileNumber;
    private UserRole role;
    private AuthProvider authProvider;
    private boolean enabled;

    public static UserRecordResponse fromEntity(UserEntity user) {
        return new UserRecordResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getMobileNumber(),
                user.getRole(),
                user.getAuthProvider(),
                user.isEnabled()
        );
    }
}
