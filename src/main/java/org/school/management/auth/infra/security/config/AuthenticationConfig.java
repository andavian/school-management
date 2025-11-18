package org.school.management.auth.infra.security.config;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.infra.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(springPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public HashedPassword.PasswordEncoder domainPasswordEncoder() {
        return new HashedPassword.PasswordEncoder() {
            @Override
            public String encode(String plainPassword) {
                // podrías reutilizar BCrypt
                return springPasswordEncoder().encode(plainPassword);
            }

            @Override
            public boolean matches(String plainPassword, String hashedPassword) {
                return springPasswordEncoder().matches(plainPassword, hashedPassword);
            }
        };
    }
}
