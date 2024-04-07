package com.epam.crmgym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionMangConfig -> sessionMangConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Adjust the endpoints according to your project logic
                        .requestMatchers(HttpMethod.POST, "/api/trainees/fortest").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/trainees/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/trainees/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/trainers/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/trainers/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/trainers/**").authenticated()
                        .anyRequest().denyAll()
                ).csrf(csrf -> csrf.disable());

        return http.build();
    }
}
