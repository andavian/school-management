// org.school.management.attendance.application.dto.response.DailyAttendanceResponse
package org.school.management.attendance.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DailyAttendanceResponse(
        UUID dailyAttendanceId,
        UUID studentId,
        UUID gradeLevelId,
        UUID academicYearId,
        LocalDate attendanceDate,
        String status,
        String justificationReason,
        String observations,
        UUID recordedByUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}