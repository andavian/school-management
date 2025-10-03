package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileApiRequest(
        @Email(message = "Email debe ser válido")
        String email,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Formato de teléfono inválido")
        String phoneNumber
) {}