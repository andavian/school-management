// src/main/java/org/school/management/resources/domain/repository/ReservationRepository.java
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

public interface ReservationRepository {

    Optional<Reservation> findByReservationId(ReservationId id);

    List<Reservation> findByResourceIdAndDate(ResourceId resourceId, LocalDate date);

    List<Reservation> findByRequesterId(UUID requesterId);   // UUID

    List<Reservation> findAllActive();

    Set<UnitId> findReservedUnitIdsForDateRange(ResourceId resourceId,
                                                LocalDate date,
                                                LocalTime start,
                                                LocalTime end);

    Reservation save(Reservation reservation);
}