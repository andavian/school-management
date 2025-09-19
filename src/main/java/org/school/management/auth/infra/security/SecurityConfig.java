package org.school.management.auth.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] PUBLIC_ENDPOINTS_WHITELIST = {
            "/api/auth/login",
            "/api/auth/signup",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",

    };
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/check-login").hasAnyRole("ADMIN_NO_COUNTRY", "ADMIN_VOS_Y_TU_VOZ", "ENTREPRENEUR", "COMPANY_LEADER")
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN_NO_COUNTRY", "ADMIN_VOS_Y_TU_VOZ")
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/leads", "/api/entrepreneurs", "/api/companies").permitAll()
                        .requestMatchers(HttpMethod.PUT,  "/api/entrepreneurs/{id}", "/api/companies/{id}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/leads/{id}/unsubscribe", "/api/entrepreneurs/{id}/unsubscribe", "/api/companies/{id}/unsubscribe").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://vocaltech-dashboard.vercel.app/", "http://localhost:5173", "http://localhost:4321", "http://localhost:8090", "https://vocaltech-production.up.railway.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "Accept", "*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

