package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.*;


public record CreateOrientationRequest (
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
String name,

                @NotBlank(message = "Code is required")
@Size(min = 2, max = 20, message = "Code must be between 2 and 20 characters")
@Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must contain only uppercase letters, numbers, and underscores")
String code,

@Size(max = 500, message = "Description must not exceed 500 characters")
String description,

@NotNull(message = "Available from year is required")
@Min(value = 1, message = "Available from year must be >= 1")
@Max(value = 7, message = "Available from year must be <= 7")
Integer availableFromYear
){


}
