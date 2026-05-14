package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.InvalidReservationStateException;
import org.school.management.resources.domain.exception.ReservationNotFoundException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("MarkReservationInUseUseCase")
class MarkReservationInUseUseCaseTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private MarkReservationInUseUseCase useCase;

    private static final UUID RESERVATION_UUID = UUID.randomUUID();
    private static final UUID ACTOR_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — marca reserva como IN_USE")
    void execute_happyPath_marksAsInUse() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.getStatus()).thenReturn(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByReservationId(any(ReservationId.class))).thenReturn(Optional.of(reservation));
        doNothing().when(reservation).markAsInUse();
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(mapper.toReservationResponse(any(Reservation.class))).thenReturn(mock(ReservationResponse.class));

        ReservationResponse result = useCase.execute(RESERVATION_UUID, ACTOR_ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — estado distinto de CONFIRMED — lanza InvalidReservationStateException")
    void execute_wrongStatus_throwsException() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.getStatus()).thenReturn(ReservationStatus.IN_USE);
        when(reservation.getReservationId()).thenReturn(ReservationId.from(RESERVATION_UUID));

        when(reservationRepository.findByReservationId(any(ReservationId.class))).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> useCase.execute(RESERVATION_UUID, ACTOR_ID))
                .isInstanceOf(InvalidReservationStateException.class);
    }

    @Test
    @DisplayName("execute — no encontrada — lanza ReservationNotFoundException")
    void execute_notFound_throwsException() {
        when(reservationRepository.findByReservationId(any(ReservationId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RESERVATION_UUID, ACTOR_ID))
                .isInstanceOf(ReservationNotFoundException.class);
    }
}
