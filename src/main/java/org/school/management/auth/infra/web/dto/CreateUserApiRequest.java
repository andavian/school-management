package org.school.management.auth.infra.web.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.Set;

@Data
@Builder
public class CreateUserApiRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit and one special character")
    private String password;

    @NotEmpty(message = "At least one role is required")
    @Size(min = 1, max = 5, message = "Must have between 1 and 5 roles")
    private Set<@Pattern(regexp = "^(ADMIN|TEACHER|STUDENT|PARENT|STAFF)$",
            message = "Invalid role. Valid roles: ADMIN, TEACHER, STUDENT, PARENT, STAFF") String> roles;
}
