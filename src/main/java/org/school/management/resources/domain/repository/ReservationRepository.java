package org.school.management.resources.domain.repository;

import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Puerto de dominio para persistencia de reservas.
 * Implementado por ReservationRepositoryAdapter en infrastructure/persistence/adapter/
 */
public interface ReservationRepository {

    Optional<Reservation> findByReservationId(ReservationId id);

    List<Reservation> findByResourceIdAndDate(ResourceId resourceId, LocalDate date);

    /**
     * Usa UUID crudo para evitar acoplamiento directo con auth/ en el dominio.
     * Cumple la regla: "sin FK cross-BC a users — solo UUID".
     */
    List<Reservation> findByRequesterId(UUID requesterId);

    /**
     * Retorna solo reservas en estado CONFIRMED o IN_USE.
     */
    List<Reservation> findAllActive();

    /**
     * Consulta crítica para el Domain Service:
     * Identifica qué unidades físicas ya están asignadas a reservas activas
     * (CONFIRMED/IN_USE) que se solapan en fecha y horario.
     */
    Set<UnitId> findReservedUnitIdsForDateRange(ResourceId resourceId, LocalDate date, LocalTime start, LocalTime end);

    Reservation save(Reservation reservation);
}