package org.school.management.grades.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PeriodGradeResponse(
        UUID periodGradeId,
        UUID studentCourseSubjectId,
        UUID periodId,
        BigDecimal averageGrade,
        BigDecimal adjustedGrade,
        BigDecimal finalPeriodGrade,
        Boolean isPassed,
        boolean isValidated,
        UUID validatedBy,
        LocalDateTime validatedAt,
        String observations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}