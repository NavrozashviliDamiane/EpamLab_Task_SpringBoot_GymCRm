package com.epam.crmgym.controller;

import com.epam.crmgym.config.JwtService;
import com.epam.crmgym.dto.user.LoginRequest;
import com.epam.crmgym.entity.User;
import com.epam.crmgym.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthenticateController {


    private final JwtService jwtService;
    private final UserRepository userRepository;


    public AuthenticateController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;

    }

    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for username: {}", loginRequest.getUsername());

        boolean isAuthenticated = jwtService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (isAuthenticated) {
            log.info("User '{}' successfully authenticated.", loginRequest.getUsername());
            User user = userRepository.findByUsername(loginRequest.getUsername());
            Map<String, Object> extraClaims = new HashMap<>();
            String token = jwtService.generateToken(user, extraClaims);
            log.debug("Generated JWT token for user '{}': {}", loginRequest.getUsername(), token);
            return ResponseEntity.ok(token);
        } else {
            log.warn("Authentication failed for user '{}'.", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}