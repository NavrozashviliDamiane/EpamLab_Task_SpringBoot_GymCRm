package com.epam.crmgym.config;



import com.epam.crmgym.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Map<String, Integer> unsuccessfulLoginAttempts;

    @Mock
    private Map<String, Long> blockedIPs;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testDoFilterInternal_WithValidTokenAndRequestBody() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testUser");

        String encodedPassword = passwordEncoder.encode("password");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testUser", encodedPassword, Collections.emptyList());

        when(jwtService.validateToken("valid_token")).thenReturn(true);
        when(jwtService.extractUsername("valid_token")).thenReturn("testUser");
        when(jwtService.loadUserByUsername("testUser")).thenReturn(userDetails);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertTrue(response.getStatus() == HttpServletResponse.SC_OK);
        verify(filterChain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }



    @Test
    void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_token");

        when(jwtService.validateToken("invalid_token")).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertTrue(response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED);
        assertEquals("Invalid token", response.getErrorMessage());
    }
}
