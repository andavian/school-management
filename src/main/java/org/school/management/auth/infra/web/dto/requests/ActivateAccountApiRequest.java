package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActivateAccountApiRequest(
        @NotBlank(message = "Token es requerido")
        String token,

        @NotBlank(message = "Nueva contraseña es requerida")
        @Size(min = 8, max = 128, message = "Password debe tener entre 8 y 128 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+$",
                message = "Password debe contener mayúscula, minúscula, número y carácter especial")
        String newPassword
) {}