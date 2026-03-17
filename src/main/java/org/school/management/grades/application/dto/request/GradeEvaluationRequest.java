package org.school.management.grades.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record GradeEvaluationRequest(

        @NotNull(message = "grade is required")
        @DecimalMin(value = "0.00", message = "grade must be at least 0")
        @DecimalMax(value = "10.00", message = "grade must not exceed 10")
        BigDecimal grade,

        @Size(max = 1000, message = "observations must not exceed 1000 characters")
        String teacherObservations
) {}