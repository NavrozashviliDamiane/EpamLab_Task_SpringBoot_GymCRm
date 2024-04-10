package com.epam.crmgym.config;

import com.epam.crmgym.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final long BLOCK_DURATION = 5 * 60 * 1000; // 5 minutes
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Map<String, Integer> unsuccessfulLoginAttempts;
    @Autowired
    private Map<String, Long> blockedIPs;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        String authHeader = request.getHeader("Authorization");

        String clientIP = getClientIP(request);

        if (isIPBlocked(clientIP)) {
            log.warn("IP '{}' is blocked due to too many unsuccessful login attempts.", clientIP);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        String requestURI = request.getRequestURI();

        String[] publicEndpoints = {"/api/authentication", "/api/trainees/register", "/api/trainers/register", "/error"};

        boolean isPublicEndpoint = Arrays.stream(publicEndpoints).anyMatch(requestURI::startsWith);

        if (!isPublicEndpoint) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("Token not provided.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token not provided");
                return;
            }

            String jwt = authHeader.substring(7);
            log.info("Generated token.{}", jwt);

            if (!jwtService.validateToken(jwt)) {
                log.info("Invalid token.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String username = jwtService.extractUsername(jwt);
            UserDetails userDetails = jwtService.loadUserByUsername(username);

            String requestBodyContent = new String(wrappedRequest.getRequestBody());

            log.info("Request body size: {}", requestBodyContent.length());
            log.info("Request body content: {}", requestBodyContent);

            if (!requestBodyContent.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestBody = objectMapper.readValue(requestBodyContent, new TypeReference<Map<String, String>>() {
                });
                String requestBodyUsername = requestBody.get("username");
                log.info("Username from request body: {}", requestBodyUsername);

                if (requestBodyUsername != null && requestBodyUsername.equals(userDetails.getUsername())) {
                    if (!jwtService.isTokenBlacklisted(jwt)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.info("JWT token is blacklisted. Access denied.");
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(wrappedRequest, response);
    }


    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }

    private boolean isIPBlocked(String clientIP) {
        Long unblockTime = blockedIPs.get(clientIP);
        return unblockTime != null && unblockTime > System.currentTimeMillis();
    }
}