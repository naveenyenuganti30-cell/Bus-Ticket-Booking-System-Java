package com.seatreserve.busbooking.service;

import com.seatreserve.busbooking.config.JwtService;
import com.seatreserve.busbooking.exception.InvalidBookingException;
import com.seatreserve.busbooking.exception.ResourceNotFoundException;
import com.seatreserve.busbooking.model.dto.AuthDtos;
import com.seatreserve.busbooking.model.entity.User;
import com.seatreserve.busbooking.model.enums.Role;
import com.seatreserve.busbooking.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidBookingException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User(
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                request.getPhone(),
                Role.ROLE_USER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthDtos.AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = jwtService.generateToken(user);
        return new AuthDtos.AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public AuthDtos.UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new AuthDtos.UserProfileResponse(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(), user.getRole().name());
    }
}
