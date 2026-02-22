package com.jobportal.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.jobportal.auth.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh-token").permitAll()
                .requestMatchers("/api/auth/me", "/api/auth/change-password", "/api/auth/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasRole("EMPLOYER")
                .requestMatchers("/api/employers/profile").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.GET, "/api/employers/{id:\\d+}", "/api/employers/{id:\\d+}/jobs").permitAll()
                .requestMatchers("/api/employers/**").hasRole("EMPLOYER")
                .requestMatchers("/api/candidates/profile").hasRole("CANDIDATE")
                .requestMatchers(HttpMethod.GET, "/api/candidates/{id:\\d+}", "/api/candidates/{id:\\d+}/resume").permitAll()
                .requestMatchers("/api/candidates/**").hasRole("CANDIDATE")
                .requestMatchers("/api/applications/**").authenticated()
                .requestMatchers("/api/saved-jobs/**").hasRole("CANDIDATE")
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers("/api/categories/**").hasRole("EMPLOYER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
