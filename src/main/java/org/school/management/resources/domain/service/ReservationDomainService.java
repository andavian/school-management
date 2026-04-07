package org.school.management.resources.domain.service;

import org.school.management.resources.domain.exception.InsufficientResourceUnitsException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Domain Service encargado de orquestar la lógica de disponibilidad
 * y asignación física de unidades a reservas.
 * Esta lógica cruza dos agregados (Reservation y ResourceUnit).
 */
public class ReservationDomainService {

    private final ResourceUnitRepository resourceUnitRepository;
    private final ReservationRepository reservationRepository;

    public ReservationDomainService(ResourceUnitRepository resourceUnitRepository,
                                    ReservationRepository reservationRepository) {
        this.resourceUnitRepository = resourceUnitRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Valida disponibilidad en el rango horario y asigna las unidades físicas.
     * Actualiza el estado de las unidades a IN_USE y enlaza ReservationUnits.
     * Lanza InsufficientResourceUnitsException si no hay stock suficiente.
     */
    public void confirmAndAssignUnits(Reservation reservation) {
        ResourceId resourceId = reservation.getResourceId();
        LocalDate date = reservation.getReservationDate();
        LocalTime start = reservation.getStartTime();
        LocalTime end = reservation.getEndTime();
        int quantity = reservation.getQuantityRequested();

        // 1. Obtener IDs de unidades ya reservadas en ese horario (estado CONFIRMED o IN_USE)
        Set<UnitId> busyUnitIds = reservationRepository.findReservedUnitIdsForDateRange(resourceId, date, start, end);

        // 2. Obtener todas las unidades del tipo en estado AVAILABLE
        List<ResourceUnit> allAvailableUnits = resourceUnitRepository.findByResourceIdAndStatus(resourceId, UnitStatus.AVAILABLE);

        // 3. Filtrar las que están realmente libres en ese horario
        List<ResourceUnit> freeUnits = allAvailableUnits.stream()
                .filter(unit -> !busyUnitIds.contains(unit.getUnitId()))
                .limit(quantity)
                .collect(Collectors.toList());

        if (freeUnits.size() < quantity) {
            throw InsufficientResourceUnitsException.withDetails(resourceId, quantity, freeUnits.size(), date, start, end);
        }

        // 4. Asignar unidades a la reserva y transicionar su estado operativo
        for (ResourceUnit unit : freeUnits) {
            reservation.assignUnit(unit); // Crea el enlace ReservationUnit
            unit.assignToReservation();   // Transición AVAILABLE → IN_USE
        }
    }
}