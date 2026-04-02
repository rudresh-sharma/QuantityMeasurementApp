package com.app.userservice.controller;

import com.app.userservice.dto.CreateUserRequest;
import com.app.userservice.dto.GoogleUserSyncRequest;
import com.app.userservice.dto.UserRecordResponse;
import com.app.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserRecordResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserRecordResponse> getByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PutMapping("/google-sync")
    public ResponseEntity<UserRecordResponse> syncGoogleUser(@Valid @RequestBody GoogleUserSyncRequest request) {
        return ResponseEntity.ok(userService.processGoogleUser(request));
    }
}
