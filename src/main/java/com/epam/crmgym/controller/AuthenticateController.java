package com.epam.crmgym.controller;

import com.epam.crmgym.config.JwtService;
import com.epam.crmgym.dto.user.LoginRequest;
import com.epam.crmgym.entity.User;
import com.epam.crmgym.repository.UserRepository;
import com.epam.crmgym.util.user.BlockLoginIpHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthenticateController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Map<String, Integer> unsuccessfulLoginAttempts;
    private final Map<String, Long> blockedIPs;
    private final Set<String> blacklistedTokens;
    private final BlockLoginIpHelper blockLoginIpHelper;


    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION = 5 * 60 * 1000;

    public AuthenticateController(JwtService jwtService, UserRepository userRepository, BlockLoginIpHelper blockLoginIpHelper) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.blockLoginIpHelper = blockLoginIpHelper;
        this.unsuccessfulLoginAttempts = new ConcurrentHashMap<>();
        this.blockedIPs = new ConcurrentHashMap<>();
        this.blacklistedTokens = new HashSet<>();
    }




    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Received login request for username: {} from IP: {}", loginRequest.getUsername(), blockLoginIpHelper.getClientIP(request));

        String clientIP = blockLoginIpHelper.getClientIP(request);

        if (blockLoginIpHelper.isIPBlocked(clientIP)) {
            log.warn("IP '{}' is blocked due to too many unsuccessful login attempts.", clientIP);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("IP is blocked. Try again later.");
        }

        unsuccessfulLoginAttempts.putIfAbsent(clientIP, 0);

        boolean isAuthenticated = jwtService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (isAuthenticated) {
            log.info("User '{}' successfully authenticated.", loginRequest.getUsername());
            unsuccessfulLoginAttempts.remove(clientIP);
            return blockLoginIpHelper.generateTokenResponse(loginRequest.getUsername());
        } else {
            log.warn("Authentication failed for user '{}' from IP '{}'.", loginRequest.getUsername(), clientIP);
            int attempts = unsuccessfulLoginAttempts.compute(clientIP, (key, value) -> value + 1);
            if (attempts >= MAX_ATTEMPTS) {
                blockLoginIpHelper.blockIP(clientIP);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtService.extractTokenFromRequest(request);
        if (token != null) {
            jwtService.blacklistToken(token); // Add the token to the blacklist
            log.info("JWT token blacklisted successfully: {}", token);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }




}
