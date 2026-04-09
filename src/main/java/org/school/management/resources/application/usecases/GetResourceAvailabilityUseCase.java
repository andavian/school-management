// src/main/java/org/school/management/resources/application/usecases/GetResourceAvailabilityUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetResourceAvailabilityUseCase {

    private final ResourceUnitRepository resourceUnitRepository;
    private final ReservationRepository reservationRepository;

    public record AvailabilityInfo(
            int totalAvailable,
            int currentlyReserved,
            int freeInRange,
            List<String> freeUnitCodes
    ) {}

    /**
     * Calcula disponibilidad real cruzando unidades AVAILABLE con reservas activas en el horario.
     */
    public AvailabilityInfo execute(ResourceId resourceId, LocalDate date, LocalTime start, LocalTime end) {

        // 1. Pool de unidades físicamente disponibles
        List<ResourceUnit> availablePool = resourceUnitRepository
                .findByResourceIdAndStatus(resourceId, UnitStatus.AVAILABLE);

        // 2. Unidades bloqueadas por reservas en ese rango horario
        Set<UnitId> busyUnitIds = reservationRepository
                .findReservedUnitIdsForDateRange(resourceId, date, start, end);

        // 3. Filtrar unidades realmente libres
        List<ResourceUnit> freeUnits = availablePool.stream()
                .filter(u -> !busyUnitIds.contains(u.getUnitId()))
                .toList();

        List<String> freeCodes = freeUnits.stream()
                .map(ResourceUnit::getUnitCode)
                .toList();

        log.debug("Disponibilidad para recurso {} en {} {}–{}: {} libres de {}",
                resourceId, date, start, end, freeUnits.size(), availablePool.size());

        return new AvailabilityInfo(
                availablePool.size(),
                busyUnitIds.size(),
                freeUnits.size(),
                freeCodes
        );
    }
}