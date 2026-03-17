package org.school.management.grades.application.dto.response;

import org.school.management.grades.domain.valueobject.FinalGradeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinalGradeResponse(
        UUID finalGradeId,
        UUID studentCourseSubjectId,
        UUID academicYearId,
        BigDecimal periodAverage,
        BigDecimal finalExamGrade,
        BigDecimal finalGrade,
        FinalGradeStatus status,
        boolean isValidated,
        UUID validatedBy,
        LocalDateTime validatedAt,
        boolean recordedInRegistry,
        UUID registryId,
        Integer folioNumber,
        LocalDateTime recordedAt,
        String observations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}