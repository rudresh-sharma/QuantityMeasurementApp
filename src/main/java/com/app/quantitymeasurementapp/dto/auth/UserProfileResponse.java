package com.app.quantitymeasurementapp.dto.auth;

import com.app.quantitymeasurementapp.model.AuthProvider;
import com.app.quantitymeasurementapp.model.UserEntity;
import com.app.quantitymeasurementapp.model.UserRole;
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
