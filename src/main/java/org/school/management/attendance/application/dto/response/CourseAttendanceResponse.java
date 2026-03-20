// org.school.management.attendance.application.dto.response.CourseAttendanceResponse
package org.school.management.attendance.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CourseAttendanceResponse(
        UUID courseAttendanceId,
        UUID studentCourseSubjectId,
        UUID courseSubjectId,
        UUID periodId,
        LocalDate classDate,
        String status,
        double absenceWeight,
        String observations,
        UUID recordedByUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}