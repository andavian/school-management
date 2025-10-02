package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.*;
import java.util.Set;

public record CreateUserApiRequest(
            @NotBlank(message = "DNI es requerido")
            @Pattern(regexp = "^\\d{7,8}$", message = "DNI debe tener 7 u 8 dígitos")
            String dni,

            @NotBlank(message = "Password is required")
            @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+$",
                    message = "Password must contain at least one uppercase letter, one lowercase letter, one digit and one special character")
            String password,

            @NotEmpty(message = "At least one role is required")
            @Size(min = 1, max = 5, message = "Must have between 1 and 5 roles")
            Set<@Pattern(regexp = "^(ADMIN|TEACHER|STUDENT|PARENT|STAFF)$",
                    message = "Invalid role. Valid roles: ADMIN, TEACHER, STUDENT, PARENT, STAFF") String> roles
) {}
