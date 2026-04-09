// src/main/java/org/school/management/resources/application/usecases/ReturnReservationUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.ReturnReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.InvalidReservationStateException;
import org.school.management.resources.domain.exception.ReservationNotFoundException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationStatus;
import org.school.management.resources.domain.valueobject.UnitStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ResourceUnitRepository resourceUnitRepository;
    private final ResourceApplicationMapper mapper;

    /**
     * Registra la devolución de una reserva (IN_USE → RETURNED)
     * y libera las unidades físicas asignadas.
     */
    @Transactional
    public ReservationResponse execute(UUID reservationUuid, ReturnReservationRequest request) {

        Reservation reservation = reservationRepository.findByReservationId(ReservationId.from(reservationUuid))
                .orElseThrow(() -> ReservationNotFoundException.byId(reservationUuid));

        if (reservation.getStatus() != ReservationStatus.IN_USE) {
            throw InvalidReservationStateException.invalidTransition(
                    reservation.getReservationId(), reservation.getStatus(), "return");
        }

        String observations = (request != null) ? request.observations() : null;

        // Liberar todas las unidades físicas asignadas
        for (var assignedUnit : reservation.getAssignedUnits()) {
            ResourceUnit unit = resourceUnitRepository.findByUnitId(assignedUnit.getUnitId())
                    .orElseThrow(() -> new IllegalStateException("Unidad física no encontrada: " + assignedUnit.getUnitId()));

            if (unit.getUnitStatus() == UnitStatus.IN_USE) {
                unit.returnFromReservation();
                resourceUnitRepository.save(unit);
            }
        }

        // Marcar la reserva como devuelta
        reservation.markAsReturned(observations);

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reserva {} devuelta exitosamente. Observaciones: {}", reservationUuid, observations);
        return mapper.toReservationResponse(saved);
    }
}