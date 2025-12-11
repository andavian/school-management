package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateEvaluationPeriodRequest (
        @NotBlank(message = "Academic year ID is required")
        String academicYearId,

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
LocalDate endDate,

@NotNull(message = "Grade submission deadline is required")
LocalDate gradeSubmissionDeadline

){

 }
