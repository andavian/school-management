// org.school.management.attendance.application.dto.request.CorrectAttendanceRequest
package org.school.management.attendance.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record CorrectAttendanceRequest(

        @NotNull(message = "New status is required")
        String newStatus,

        String observations
) {}