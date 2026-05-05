package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InitRegistrySequenceRequest(
        @NotBlank(message = "Academic year ID is required")
        String academicYearId,

        @NotNull(message = "Last paper number is required")
        @Min(value = 0, message = "Last paper number must be >= 0")
        @Max(value = 998, message = "Last paper number must be <= 998")
        Integer lastPaperNumber
) {}