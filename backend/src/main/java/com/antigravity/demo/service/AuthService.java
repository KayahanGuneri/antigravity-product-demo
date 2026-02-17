package com.antigravity.demo.service;

import com.antigravity.demo.dto.AuthRequest;
import com.antigravity.demo.dto.AuthResponse;
import com.antigravity.demo.model.Role;
import com.antigravity.demo.model.User;
import com.antigravity.demo.repository.UserRepository;
import com.antigravity.demo.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${application.security.jwt.expiration-minutes}")
    private long jwtExpiration;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRequest.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Default to USER if no role provided, or use provided role
        try {
            if (request.getRole() != null) {
                user.setRole(Role.valueOf(request.getRole()));
            } else {
                user.setRole(Role.USER);
            }
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER);
        }

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(jwtToken, jwtExpiration * 60, user.getRole().name());
    }

    public AuthResponse login(AuthRequest.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jwtToken = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(jwtToken, jwtExpiration * 60, user.getRole().name());
    }
}
