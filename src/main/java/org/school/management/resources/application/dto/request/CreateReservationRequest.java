package org.school.management.resources.application.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateReservationRequest(
        @NotNull(message = "El ID del recurso es obligatorio") UUID resourceId,
        @NotNull(message = "La fecha de reserva es obligatoria") LocalDate reservationDate,
        @NotNull(message = "La hora de inicio es obligatoria") LocalTime startTime,
        @NotNull(message = "La hora de fin es obligatoria") LocalTime endTime,
        @Min(value = 1, message = "La cantidad solicitada debe ser al menos 1") int quantityRequested,
        @NotBlank(message = "El propósito es obligatorio") @Size(max = 500) String purpose,
        @Size(max = 100) String gradeLevelInfo
) {}