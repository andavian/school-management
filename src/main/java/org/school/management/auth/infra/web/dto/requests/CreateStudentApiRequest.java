package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateStudentApiRequest(
        @NotBlank(message = "DNI es requerido")
        @Pattern(regexp = "^\\d{7,8}$", message = "DNI debe tener 7 u 8 dígitos")
        String dni,

        @NotBlank(message = "Nombre es requerido")
        @Size(max = 50, message = "Nombre no puede exceder 50 caracteres")
        String firstName,

        @NotBlank(message = "Apellido es requerido")
        @Size(max = 50, message = "Apellido no puede exceder 50 caracteres")
        String lastName,

        @Email(message = "Email debe ser válido")
        String email,            // Opcional para menores

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Formato de teléfono inválido")
        String phoneNumber,

        @Email(message = "Email del padre debe ser válido")
        String parentEmail,

        @NotBlank(message = "Año/Grado es requerido")
        @Pattern(regexp = "^[1-6]$", message = "Grado debe ser entre 1 y 6")
        String grade,

        @NotBlank(message = "División es requerida")
        @Pattern(regexp = "^[A-Z]$", message = "División debe ser una letra (A-Z)")
        String division
) {}
