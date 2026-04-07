package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.InvalidReservationStateException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case para transicionar una reserva de CONFIRMED → IN_USE.
 * Se dispara cuando el personal de depósito/soporte entrega físicamente los recursos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarkReservationInUseUseCase {

    private final ReservationRepository reservationRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ReservationResponse execute(UUID reservationUuid, UUID actorId) {
        Reservation reservation = reservationRepository.findByReservationId(ReservationId.from(reservationUuid))
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + reservationUuid));

        // Validación de estado: solo se puede marcar en uso si está CONFIRMED
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw InvalidReservationStateException.invalidTransition(
                    reservation.getReservationId(), reservation.getStatus(), "mark as IN_USE");
        }

        // Transición de estado en el agregado de dominio
        reservation.markAsInUse();

        // Persistir
        Reservation saved = reservationRepository.save(reservation);

        log.info("Reserva {} marcada como IN_USE por actor {}", reservationUuid, actorId);
        return mapper.toReservationResponse(saved);
    }
}