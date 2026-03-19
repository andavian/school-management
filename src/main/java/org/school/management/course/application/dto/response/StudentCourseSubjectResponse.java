package org.school.management.course.application.dto.response;

import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentCourseSubjectResponse(
        UUID studentCourseSubjectId,
        UUID enrollmentId,
        UUID courseSubjectId,
        SubjectEnrollmentStatus status,
        int totalClasses,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}