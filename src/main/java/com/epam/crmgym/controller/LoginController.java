package com.epam.crmgym.controller;

import com.epam.crmgym.dto.user.LoginRequest;
import com.epam.crmgym.service.AuthenticateService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticateService authenticateService;

    public LoginController(AuthenticateService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @GetMapping
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = authenticateService.matchUserCredentials(loginRequest.getUsername(), loginRequest.getPassword());

        log.info("REST call made to /api/login endpoint. Request: {}", loginRequest.getUsername());

        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
