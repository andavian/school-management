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

/**
 * Use Case de consulta para calcular disponibilidad real de unidades físicas
 * en un rango horario específico. Cruza unidades operativamente disponibles
 * con reservas activas que se solapan en el tiempo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetResourceAvailabilityUseCase {

    private final ResourceUnitRepository resourceUnitRepository;
    private final ReservationRepository reservationRepository;

    /**
     * DTO interno de respuesta para este caso de uso.
     * Expone contadores y códigos de unidades libres para el frontend.
     */
    public record AvailabilityInfo(int totalAvailable, int currentlyReserved, int freeInRange, List<String> freeUnitCodes) {}

    /**
     * Calcula disponibilidad cruzando el pool de unidades AVAILABLE con reservas CONFIRMED/IN_USE.
     */
    public AvailabilityInfo execute(ResourceId resourceId, LocalDate date, LocalTime start, LocalTime end) {
        // 1. Pool base: unidades cuyo estado operativo es AVAILABLE
        List<ResourceUnit> availablePool = resourceUnitRepository.findByResourceIdAndStatus(resourceId, UnitStatus.AVAILABLE);

        // 2. Unidades bloqueadas por reservas activas en ese horario (query JPQL optimizada)
        Set<UnitId> busyUnitIds = reservationRepository.findReservedUnitIdsForDateRange(resourceId, date, start, end);

        // 3. Filtrar y contar realmente libres
        long freeCount = availablePool.stream()
                .filter(u -> !busyUnitIds.contains(u.getUnitId()))
                .count();

        List<String> freeCodes = availablePool.stream()
                .filter(u -> !busyUnitIds.contains(u.getUnitId()))
                .map(ResourceUnit::getUnitCode)
                .collect(Collectors.toList());

        return new AvailabilityInfo(availablePool.size(), busyUnitIds.size(), (int) freeCount, freeCodes);
    }
}