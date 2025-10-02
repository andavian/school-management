package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginApiRequest(
        @NotBlank(message = "DNI es requerido")
        @Pattern(regexp = "^\\d{7,8}$", message = "DNI debe tener 7 u 8 dígitos")
        String dni,              // ← CAMBIO: era email

        @NotBlank(message = "Password es requerido")
        String password,

        Boolean rememberMe
) {
    public LoginApiRequest {
        if (!rememberMe) {
            rememberMe = false;
        }
    }
}
