package org.school.management.resources.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationUnitId;
import org.school.management.resources.domain.valueobject.UnitId;

import java.time.LocalDateTime;

/**
 * Entidad de relación muchos-a-muchos entre Reservation y ResourceUnit.
 * Representa la asignación física de una unidad específica a una reserva.
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReservationUnit {

    @EqualsAndHashCode.Include
    private final ReservationUnitId reservationUnitId;
    private final ReservationId reservationId;
    private final UnitId unitId;
    private final LocalDateTime createdAt;

    public static ReservationUnit create(ReservationUnitId reservationUnitId, ReservationId reservationId, UnitId unitId) {
        if (reservationUnitId == null) throw new IllegalArgumentException("ReservationUnitId is required");
        if (reservationId == null) throw new IllegalArgumentException("ReservationId is required");
        if (unitId == null) throw new IllegalArgumentException("UnitId is required");

        return ReservationUnit.builder()
                .reservationUnitId(reservationUnitId)
                .reservationId(reservationId)
                .unitId(unitId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}