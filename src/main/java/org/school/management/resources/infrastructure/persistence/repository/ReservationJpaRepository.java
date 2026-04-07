package org.school.management.resources.infrastructure.persistence.repository;

import org.school.management.resources.domain.valueobject.ReservationStatus;
import org.school.management.resources.infrastructure.persistence.entity.ReservationEntity;
import org.school.management.resources.infrastructure.persistence.entity.ReservationUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findByResourceIdAndReservationDate(UUID resourceId, LocalDate date);

    List<ReservationEntity> findByRequesterId(UUID requesterId);

    /**
     * Consulta crítica para el cálculo de disponibilidad.
     * Devuelve los IDs de las unidades físicas asignadas a reservas activas (CONFIRMED/IN_USE)
     * que se solapan con el rango horario solicitado.
     *
     * Lógica de solapamiento: (StartA < EndB) AND (EndA > StartB)
     */
    @Query("SELECT ru.unitId FROM ReservationUnitEntity ru " +
            "JOIN ReservationEntity r ON ru.reservationId = r.reservationId " +
            "WHERE r.resourceId = :resourceId " +
            "AND r.reservationDate = :date " +
            "AND r.status IN :activeStatuses " +
            "AND (r.startTime < :end AND r.endTime > :start)")
    Set<UUID> findReservedUnitIdsForDateRange(
            @Param("resourceId") UUID resourceId,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end,
            @Param("activeStatuses") List<ReservationStatus> activeStatuses
    );
}