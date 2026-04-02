package com.app.authenticationservice.service;

import com.app.authenticationservice.client.UserServiceClient;
import com.app.authenticationservice.client.dto.CreateUserRequest;
import com.app.authenticationservice.client.dto.GoogleUserSyncRequest;
import com.app.authenticationservice.client.dto.UserRecordResponse;
import com.app.authenticationservice.dto.auth.AuthResponse;
import com.app.authenticationservice.dto.auth.LoginRequest;
import com.app.authenticationservice.dto.auth.RegisterRequest;
import com.app.authenticationservice.dto.auth.UserProfileResponse;
import com.app.authenticationservice.exception.AuthException;
import com.app.authenticationservice.model.AuthProvider;
import com.app.authenticationservice.model.UserRole;
import com.app.authenticationservice.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserServiceClient userServiceClient,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFullName(request.getFullName().trim());
        createUserRequest.setEmail(request.getEmail().trim().toLowerCase());
        createUserRequest.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        createUserRequest.setMobileNumber(request.getMobileNumber().trim());
        createUserRequest.setRole(UserRole.USER);
        createUserRequest.setAuthProvider(AuthProvider.LOCAL);
        createUserRequest.setEnabled(true);

        UserRecordResponse savedUser = userServiceClient.createUser(createUserRequest);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        UserRecordResponse user = userServiceClient.getUserByEmail(email);

        if (user.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new AuthException("This account uses Google sign-in. Please continue with Google.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new AuthException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    public UserProfileResponse getCurrentUser(String email) {
        UserRecordResponse user = userServiceClient.getUserByEmail(email);
        return UserProfileResponse.fromUserRecord(user);
    }

    public UserRecordResponse processGoogleUser(String fullName, String email) {
        GoogleUserSyncRequest syncRequest = new GoogleUserSyncRequest();
        syncRequest.setFullName(fullName);
        syncRequest.setEmail(email.trim().toLowerCase());
        return userServiceClient.syncGoogleUser(syncRequest);
    }

    private AuthResponse buildAuthResponse(UserRecordResponse user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                UserProfileResponse.fromUserRecord(user)
        );
    }
}
