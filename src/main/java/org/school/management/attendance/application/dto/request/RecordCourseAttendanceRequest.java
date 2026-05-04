// org.school.management.attendance.application.dto.request.RecordCourseAttendanceRequest
package org.school.management.attendance.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record RecordCourseAttendanceRequest(

        @NotNull(message = "StudentCourseSubjectId is required")
        UUID studentCourseSubjectId,

        @NotNull(message = "CourseSubjectId is required")
        UUID courseSubjectId,

        @NotNull(message = "PeriodId is required")
        UUID periodId,

        @NotNull(message = "ClassDate is required")
        @PastOrPresent(message = "ClassDate cannot be in the future")
        LocalDate classDate,

        @NotNull(message = "Status is required")
        String status,   // PRESENT | ABSENT | LATE | WITHDRAWN

        String observations
) {}