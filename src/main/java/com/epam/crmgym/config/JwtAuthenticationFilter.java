package com.epam.crmgym.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            log.info("Generated token.{}", jwt);

            if (jwtService.validateToken(jwt)) {
                String username = jwtService.extractUsername(jwt);
                UserDetails userDetails = jwtService.loadUserByUsername(username);

                // Read the request body content
                String requestBodyContent = new String(wrappedRequest.getRequestBody());

                log.info("Request body size: {}", requestBodyContent.length());
                log.info("Request body content: {}", requestBodyContent);

                if (!requestBodyContent.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> requestBody = objectMapper.readValue(requestBodyContent, new TypeReference<Map<String, String>>() {});
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
        }

        filterChain.doFilter(wrappedRequest, response);
    }

}
