package org.school.management.resources.application.dto.response;

import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.time.LocalDateTime;

public record ReservationResponse(
        ReservationId reservationId,
        ResourceId resourceId,
        TeacherId teacherId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String purpose,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt
) {
}
