package org.school.management.grades.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEvaluationRequest(

        @NotNull(message = "studentCourseSubjectId is required")
        UUID studentCourseSubjectId,

        @NotNull(message = "periodId is required")
        UUID periodId,

        @NotNull(message = "evaluationTypeId is required")
        UUID evaluationTypeId,

        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must not exceed 200 characters")
        String title,

        @Size(max = 1000, message = "description must not exceed 1000 characters")
        String description,

        @NotNull(message = "evaluationDate is required")
        LocalDate evaluationDate
) {}