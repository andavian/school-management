package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.*;

public record LoginApiRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        Boolean rememberMe
) {
    // Constructor compacto para valores por defecto
    public LoginApiRequest {
        if (rememberMe == null) {
            rememberMe = false;
        }
    }
}
