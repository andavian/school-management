package org.school.management.auth.infra.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.security.config.CorsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    // ============================================
    // Endpoints organizados por categorías
    // ============================================
    private static final String[] PUBLIC_AUTH_ENDPOINTS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token"
    };

    private static final String[] PUBLIC_DOCS_ENDPOINTS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    private static final String[] PUBLIC_HEALTH_ENDPOINTS = {
            "/actuator/health",
            "/actuator/info"
    };


    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configurando Security Filter Chain");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ============================================
                // Manejo mejorado de errores de autenticación
                // ============================================
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("Access denied for user: {}, path: {}",
                                    request.getRemoteUser(), request.getRequestURI());
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"success\":false,\"message\":\"Access denied\",\"errorCode\":\"ACCESS_DENIED\"}"
                            );
                        })
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ============================================
                // Autorización más granular y organizada
                // ============================================
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(PUBLIC_AUTH_ENDPOINTS).permitAll()
                        .requestMatchers(PUBLIC_DOCS_ENDPOINTS).permitAll()
                        .requestMatchers(PUBLIC_HEALTH_ENDPOINTS).permitAll()

                        // Endpoints de administración
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                        // Endpoints específicos por rol
                        .requestMatchers("/api/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/api/courses/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/api/grades/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/grades/my-grades").hasRole("STUDENT")

                        // Endpoints de autenticación protegidos
                        .requestMatchers("/api/auth/change-password").authenticated()
                        .requestMatchers("/api/auth/profile").authenticated()
                        .requestMatchers("/api/auth/logout").authenticated()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ============================================
    // CORS más flexible y seguro
    // ============================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Configurando CORS con orígenes: {}", corsProperties.getAllowedOrigins());

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "Accept", "Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization", "X-Total-Count", "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // ============================================
    // Bean para manejar URLs públicas (util para testing)
    // ============================================
    @Bean
    public List<String> publicEndpoints() {
        return Arrays.asList(PUBLIC_AUTH_ENDPOINTS);
    }
}