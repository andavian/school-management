package org.school.management.resources.application.dto.response;

import java.util.UUID;

/**
 * DTO de aplicación para la respuesta de una unidad asignada a una reserva.
 */
public record ReservationUnitResponse(
        UUID reservationUnitId,
        UUID unitId,
        String unitCode // Denormalizado para display rápido
) {}