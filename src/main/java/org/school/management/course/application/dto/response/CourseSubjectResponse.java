package org.school.management.course.application.dto.response;

import org.school.management.course.domain.valueobject.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CourseSubjectResponse(
        UUID courseSubjectId,
        UUID gradeLevelId,
        UUID subjectId,
        UUID teacherId,
        UUID academicYearId,
        String scheduleJson,
        String classroom,
        BigDecimal minPassingGrade,
        CourseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}