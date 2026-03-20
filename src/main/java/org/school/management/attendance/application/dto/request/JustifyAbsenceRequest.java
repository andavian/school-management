// org.school.management.attendance.application.dto.request.JustifyAbsenceRequest
package org.school.management.attendance.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JustifyAbsenceRequest(

        @NotBlank(message = "Justification reason is required")
        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason
) {}