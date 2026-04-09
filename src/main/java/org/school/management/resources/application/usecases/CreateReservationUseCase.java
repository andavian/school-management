// src/main/java/org/school/management/resources/application/usecases/CreateReservationUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.CreateReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.InsufficientResourceUnitsException;
import org.school.management.resources.domain.exception.ResourceNotFoundException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReservationUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceUnitRepository resourceUnitRepository;
    private final ReservationRepository reservationRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ReservationResponse execute(CreateReservationRequest request, UUID requesterId, String requesterName) {

        // 1. Validar que el recurso existe y es reservable
        Resource resource = resourceRepository.findByResourceId(ResourceId.of(request.resourceId()))
                .orElseThrow(() -> ResourceNotFoundException.byId(request.resourceId()));

        if (!resource.isReservable()) {
            throw new IllegalStateException("El recurso no permite reservas");
        }

        // 2. Crear la reserva (estado inicial CONFIRMED)
        Reservation reservation = Reservation.create(
                resource.getResourceId(),
                requesterId,
                requesterName,
                request.reservationDate(),
                request.startTime(),
                request.endTime(),
                request.quantityRequested(),
                request.purpose(),
                request.gradeLevelInfo()
        );

        // 3. Asignar unidades físicas disponibles
        assignAvailableUnits(reservation, request.reservationDate(),
                request.startTime(), request.endTime());

        // 4. Persistir todo (reserva + unidades asignadas)
        Reservation saved = reservationRepository.save(reservation);

        log.info("Reserva creada exitosamente: {} | Recurso: {} | Unidades asignadas: {}",
                saved.getReservationId(), resource.getCode(), saved.getAssignedUnits().size());

        return mapper.toReservationResponse(saved);
    }

    /**
     * Lógica de disponibilidad y asignación de unidades físicas.
     */
    private void assignAvailableUnits(Reservation reservation, LocalDate date,
                                      LocalTime start, LocalTime end) {

        ResourceId resourceId = reservation.getResourceId();
        int quantityRequested = reservation.getQuantityRequested();

        // Unidades ya ocupadas en ese horario
        Set<UnitId> busyUnitIds = reservationRepository
                .findReservedUnitIdsForDateRange(resourceId, date, start, end);

        // Unidades físicamente disponibles
        List<ResourceUnit> availableUnits = resourceUnitRepository
                .findByResourceIdAndStatus(resourceId, UnitStatus.AVAILABLE);

        // Filtrar las realmente libres y limitar a la cantidad solicitada
        List<ResourceUnit> freeUnits = availableUnits.stream()
                .filter(unit -> !busyUnitIds.contains(unit.getUnitId()))
                .limit(quantityRequested)
                .collect(Collectors.toList());

        if (freeUnits.size() < quantityRequested) {
            throw InsufficientResourceUnitsException.withDetails(
                    resourceId, quantityRequested, freeUnits.size(), date, start, end);
        }

        // Asignar unidades y cambiar su estado a IN_USE
        for (ResourceUnit unit : freeUnits) {
            reservation.assignUnit(unit);
            unit.assignToReservation();
            resourceUnitRepository.save(unit);   // Actualizar estado de la unidad
        }
    }
}