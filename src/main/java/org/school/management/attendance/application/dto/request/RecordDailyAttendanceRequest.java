// org.school.management.attendance.application.dto.request.RecordDailyAttendanceRequest
package org.school.management.attendance.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record RecordDailyAttendanceRequest(

        @NotNull(message = "StudentId is required")
        UUID studentId,

        @NotNull(message = "GradeLevelId is required")
        UUID gradeLevelId,

        @NotNull(message = "AcademicYearId is required")
        UUID academicYearId,

        @NotNull(message = "AttendanceDate is required")
        @PastOrPresent(message = "AttendanceDate cannot be in the future")
        LocalDate attendanceDate,

        @NotNull(message = "Status is required")
        String status,   // PRESENT | ABSENT | LATE | WITHDRAWN

        String observations
) {}