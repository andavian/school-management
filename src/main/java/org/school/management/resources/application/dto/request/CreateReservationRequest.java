package org.school.management.resources.application.dto.request;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String purpose
) {
}
