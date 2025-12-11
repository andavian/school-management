package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.*;


public record CreateGradeLevelRequest (
        @NotBlank(message = "Academic year ID is required")
        String academicYearId,

                @NotNull(message = "Year level is required")
@Min(value = 1, message = "Year level must be >= 1")
@Max(value = 7, message = "Year level must be <= 7")
Integer yearLevel,

@NotBlank(message = "Division is required")
@Pattern(regexp = "^[A-Z]$", message = "Division must be a single uppercase letter")
String division,

// Nullable - solo para años 4-7
String orientationId,

@NotBlank(message = "Shift is required")
@Pattern(regexp = "^(MORNING|AFTERNOON|EVENING)$", message = "Invalid shift")
String shift,

@NotNull(message = "Max students is required")
@Min(value = 1, message = "Max students must be >= 1")
@Max(value = 50, message = "Max students must be <= 50")
Integer maxStudents
){


}
