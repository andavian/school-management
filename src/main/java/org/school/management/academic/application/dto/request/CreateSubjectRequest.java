package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record CreateSubjectRequest (
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

                @NotBlank(message = "Code is required")
@Size(min = 2, max = 20, message = "Code must be between 2 and 20 characters")
@Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must contain only uppercase letters, numbers, and underscores")
String code,

@NotNull(message = "Year level is required")
@Min(value = 1, message = "Year level must be >= 1")
@Max(value = 7, message = "Year level must be <= 7")
Integer yearLevel,

// Nullable - si es null, es materia común
String orientationId,

Boolean isMandatory,

@NotNull(message = "Weekly hours is required")
@Min(value = 1, message = "Weekly hours must be >= 1")
@Max(value = 20, message = "Weekly hours must be <= 20")
Integer weeklyHours,

@Size(max = 500, message = "Description must not exceed 500 characters")
String description
){


}
