package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.model.dto.AuthDtos;
import com.seatreserve.busbooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        AuthDtos.AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        AuthDtos.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthDtos.UserProfileResponse> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthDtos.UserProfileResponse profile = authService.getProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }
}
