package com.epam.crmgym.config;

import com.epam.crmgym.entity.User;
import com.epam.crmgym.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Wrap the request to cache the request body
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7); // Extracting JWT token excluding "Bearer "

            if (jwtService.validateToken(jwt)) {
                String username = jwtService.extractUsername(jwt);
                UserDetails userDetails = jwtService.loadUserByUsername(username);

                // Read the request body content
                String requestBodyContent = new String(wrappedRequest.getRequestBody());

                // Log the size and content of the request body
                log.info("Request body size: {}", requestBodyContent.length());
                log.info("Request body content: {}", requestBodyContent);

                // Get the username from the request body
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestBody = objectMapper.readValue(requestBodyContent, new TypeReference<Map<String, String>>() {});
                String requestBodyUsername = requestBody.get("username");
                log.info("Username from request body: {}", requestBodyUsername);

                if (requestBodyUsername != null && requestBodyUsername.equals(userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continue the filter chain
        filterChain.doFilter(wrappedRequest, response);
    }

    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

        private final byte[] requestBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            InputStream requestInputStream = request.getInputStream();
            this.requestBody = StreamUtils.copyToByteArray(requestInputStream);
        }

        public byte[] getRequestBody() {
            return this.requestBody;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(new ByteArrayInputStream(this.requestBody));
        }

        @Override
        public BufferedReader getReader() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.requestBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream));
        }
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(InputStream cachedBody) {
            this.cachedBodyInputStream = cachedBody;
        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}


