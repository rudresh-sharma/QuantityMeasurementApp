package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.dto.auth.AuthResponse;
import com.app.quantitymeasurementapp.dto.auth.LoginRequest;
import com.app.quantitymeasurementapp.dto.auth.RegisterRequest;
import com.app.quantitymeasurementapp.dto.auth.UserProfileResponse;
import com.app.quantitymeasurementapp.exception.AuthException;
import com.app.quantitymeasurementapp.exception.DuplicateEmailException;
import com.app.quantitymeasurementapp.model.AuthProvider;
import com.app.quantitymeasurementapp.model.UserEntity;
import com.app.quantitymeasurementapp.model.UserRole;
import com.app.quantitymeasurementapp.repository.UserRepository;
import com.app.quantitymeasurementapp.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("Email already exists. Please login with your existing account.");
        }

        UserEntity user = new UserEntity();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setRole(UserRole.USER);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setEnabled(true);

        UserEntity savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthException("Invalid email or password"));

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
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthException("User not found"));
        return UserProfileResponse.fromEntity(user);
    }

    public UserEntity processGoogleUser(String fullName, String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(existing -> {
                    if (fullName != null && !fullName.isBlank()) {
                        existing.setFullName(fullName.trim());
                    }
                    if (existing.getAuthProvider() == null) {
                        existing.setAuthProvider(AuthProvider.GOOGLE);
                    }
                    existing.setEnabled(true);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setFullName(fullName == null || fullName.isBlank() ? normalizedEmail : fullName.trim());
                    user.setEmail(normalizedEmail);
                    user.setRole(UserRole.USER);
                    user.setAuthProvider(AuthProvider.GOOGLE);
                    user.setEnabled(true);
                    return userRepository.save(user);
                });
    }

    private AuthResponse buildAuthResponse(UserEntity user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                UserProfileResponse.fromEntity(user)
        );
    }
}
