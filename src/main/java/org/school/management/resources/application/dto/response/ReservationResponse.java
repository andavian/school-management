// src/main/java/org/school/management/resources/application/dto/response/ReservationResponse.java
package org.school.management.resources.application.dto.response;

import org.school.management.resources.domain.valueobject.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de aplicación para respuestas de reservas.
 */
public record ReservationResponse(
        UUID reservationId,
        UUID resourceId,
        UUID requesterId,                    // UUID en lugar de UserId
        String requesterName,
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        int quantityRequested,
        String purpose,
        String gradeLevelInfo,
        ReservationStatus status,
        String cancellationReason,
        UUID cancelledBy,                    // UUID
        String returnObservations,
        LocalDateTime returnedAt,
        List<ReservationUnitResponse> assignedUnits,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}