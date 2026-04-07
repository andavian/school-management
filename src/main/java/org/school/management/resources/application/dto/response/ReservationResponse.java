package org.school.management.resources.application.dto.response;

import org.school.management.resources.domain.valueobject.ReservationStatus;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de aplicación para respuestas de reservas.
 * Record inmutable — mapeado desde el agregado Reservation vía ResourceApplicationMapper.
 */
public record ReservationResponse(
        UUID reservationId,
        UUID resourceId,
        UUID requesterId,
        String requesterName,           // Desnormalizado para display sin cruzar BC auth/
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        int quantityRequested,
        String purpose,
        String gradeLevelInfo,
        ReservationStatus status,
        String cancellationReason,
        UUID cancelledBy,
        String returnObservations,
        LocalDateTime returnedAt,
        List<ReservationUnitResponse> assignedUnits,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}