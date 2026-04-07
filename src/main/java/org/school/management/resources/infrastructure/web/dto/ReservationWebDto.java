package org.school.management.resources.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import org.school.management.resources.domain.valueobject.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public final class ReservationWebDto {
    private ReservationWebDto() {}

    public record CreateReservationWebRequest(
            @NotNull UUID resourceId,
            @NotNull LocalDate reservationDate,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime,
            @Min(1) int quantityRequested,
            @NotBlank @Size(max = 500) String purpose,
            @Size(max = 100) String gradeLevelInfo
    ) {}

    public record CancelReservationWebRequest(
            @NotBlank @Size(min = 10, max = 500) String reason
    ) {}

    public record ReturnReservationWebRequest(
            @Size(max = 500) String observations
    ) {}

    public record ReservationWebResponse(
            UUID reservationId,
            UUID resourceId,
            String resourceCode, // Se inyecta desde el servicio si es necesario
            UUID requesterId,
            String requesterName,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            int quantityRequested,
            String purpose,
            String gradeLevelInfo,
            ReservationStatus status,
            List<ReservationUnitResponse> assignedUnits,
            LocalDateTime createdAt
    ) {}

    public record ReservationUnitResponse(
            UUID unitId,
            String unitCode
    ) {}
}