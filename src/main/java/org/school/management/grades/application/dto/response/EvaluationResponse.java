package org.school.management.grades.application.dto.response;

import org.school.management.grades.domain.valueobject.EvaluationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EvaluationResponse(
        UUID evaluationId,
        UUID studentCourseSubjectId,
        UUID periodId,
        UUID evaluationTypeId,
        String title,
        String description,
        LocalDate evaluationDate,
        BigDecimal grade,
        BigDecimal maxGrade,
        EvaluationStatus status,
        boolean isValidated,
        UUID validatedBy,
        LocalDateTime validatedAt,
        String teacherObservations,
        String adminNotes,
        boolean isPassed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}