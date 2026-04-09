// src/main/java/org/school/management/resources/application/usecases/CancelReservationUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.InvalidReservationStateException;
import org.school.management.resources.domain.exception.ReservationAccessDeniedException;
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
public class CancelReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ResourceUnitRepository resourceUnitRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ReservationResponse execute(UUID reservationUuid, UUID actorId, String reason) {

        Reservation reservation = reservationRepository.findByReservationId(ReservationId.from(reservationUuid))
                .orElseThrow(() -> ReservationNotFoundException.byId(reservationUuid));

        // Ownership + autorización
        if (!reservation.belongsToRequester(actorId)) {
            // ADMIN y STAFF pueden cancelar cualquier reserva (según @PreAuthorize en controller)
            // Aquí solo validamos que si no es owner, debe tener permisos elevados (ya validado en controller)
            throw ReservationAccessDeniedException.notOwner();
        }

        if (!reservation.getStatus().isCancelable()) {
            throw InvalidReservationStateException.invalidTransition(
                    reservation.getReservationId(), reservation.getStatus(), "cancel");
        }

        // Liberar unidades
        for (var assignedUnit : reservation.getAssignedUnits()) {
            ResourceUnit unit = resourceUnitRepository.findByUnitId(assignedUnit.getUnitId())
                    .orElseThrow(() -> new IllegalStateException("Unidad física no encontrada"));

            if (unit.getUnitStatus() == UnitStatus.IN_USE) {
                unit.returnFromReservation();
                resourceUnitRepository.save(unit);
            }
        }

        reservation.cancel(actorId, reason);
        Reservation saved = reservationRepository.save(reservation);

        log.info("Reserva {} cancelada por {} - Motivo: {}", reservationUuid, actorId, reason);
        return mapper.toReservationResponse(saved);
    }
}