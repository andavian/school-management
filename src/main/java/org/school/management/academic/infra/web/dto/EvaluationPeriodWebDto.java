package org.school.management.academic.infra.web.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public final class EvaluationPeriodWebDto {

    private EvaluationPeriodWebDto() {}

    public record CreateEvaluationPeriodWebRequest(
            @NotNull(message = "Period number is required")
            @Min(value = 1, message = "Period number must be >= 1")
            @Max(value = 4, message = "Period number must be <= 4")
            Integer periodNumber,

            @NotBlank(message = "Name is required")
            @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
            String name,

            @NotNull(message = "Start date is required")
            LocalDate startDate,

            @NotNull(message = "End date is required")
            LocalDate endDate
    ) {}

    public record EvaluationPeriodWebResponse(
            String periodId,
            String academicYearId,
            Integer periodNumber,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Boolean isInProgress,
            String status,
            java.time.LocalDateTime createdAt
    ) {}
}