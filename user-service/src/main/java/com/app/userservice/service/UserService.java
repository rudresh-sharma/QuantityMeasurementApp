package com.app.userservice.service;

import com.app.userservice.dto.CreateUserRequest;
import com.app.userservice.dto.GoogleUserSyncRequest;
import com.app.userservice.dto.UserProfileResponse;
import com.app.userservice.dto.UserRecordResponse;
import com.app.userservice.exception.DuplicateEmailException;
import com.app.userservice.exception.UserNotFoundException;
import com.app.userservice.model.AuthProvider;
import com.app.userservice.model.UserEntity;
import com.app.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRecordResponse createUser(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("Email already exists. Please login with your existing account.");
        }

        UserEntity user = new UserEntity();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(request.getPasswordHash());
        user.setMobileNumber(request.getMobileNumber() == null ? null : request.getMobileNumber().trim());
        user.setRole(request.getRole());
        user.setAuthProvider(request.getAuthProvider());
        user.setEnabled(request.isEnabled());
        return UserRecordResponse.fromEntity(userRepository.save(user));
    }

    public UserRecordResponse getByEmail(String email) {
        return UserRecordResponse.fromEntity(findEntityByEmail(email));
    }

    public UserProfileResponse getProfileByEmail(String email) {
        return UserProfileResponse.fromEntity(findEntityByEmail(email));
    }

    public UserRecordResponse processGoogleUser(GoogleUserSyncRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(existing -> {
                    if (request.getFullName() != null && !request.getFullName().isBlank()) {
                        existing.setFullName(request.getFullName().trim());
                    }
                    if (existing.getAuthProvider() == null) {
                        existing.setAuthProvider(AuthProvider.GOOGLE);
                    }
                    existing.setEnabled(true);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    UserEntity created = new UserEntity();
                    created.setFullName(request.getFullName() == null || request.getFullName().isBlank()
                            ? normalizedEmail : request.getFullName().trim());
                    created.setEmail(normalizedEmail);
                    created.setRole(com.app.userservice.model.UserRole.USER);
                    created.setAuthProvider(AuthProvider.GOOGLE);
                    created.setEnabled(true);
                    return userRepository.save(created);
                });
        return UserRecordResponse.fromEntity(user);
    }

    private UserEntity findEntityByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
