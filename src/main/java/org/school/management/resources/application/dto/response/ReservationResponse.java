package org.school.management.resources.application.dto.response;

import org.school.management.resources.domain.valueobject.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservationResponse(
        String reservationId,
        String resourceId,
        String resourceCode,
        String requesterId,
        String requesterName,
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        int quantityRequested,
        String purpose,
        String gradeLevelInfo,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}