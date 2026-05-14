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
import org.school.management.resources.domain.exception.ReservationNotFoundException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.ReservationUnit;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationStatus;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CancelReservationUseCase")
class CancelReservationUseCaseTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ResourceUnitRepository resourceUnitRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private CancelReservationUseCase useCase;

    private static final UUID RESERVATION_UUID = UUID.randomUUID();
    private static final UUID ACTOR_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — cancela reserva")
    void execute_happyPath_cancelsReservation() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.belongsToRequester(ACTOR_ID)).thenReturn(true);
        when(reservation.getStatus()).thenReturn(ReservationStatus.CONFIRMED);
        when(reservation.getAssignedUnits()).thenReturn(Collections.emptyList());

        when(reservationRepository.findByReservationId(any(ReservationId.class))).thenReturn(Optional.of(reservation));
        doNothing().when(reservation).cancel(any(), any());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(mapper.toReservationResponse(any(Reservation.class))).thenReturn(mock(ReservationResponse.class));

        ReservationResponse result = useCase.execute(RESERVATION_UUID, ACTOR_ID, "Ya no se necesita");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrada — lanza ReservationNotFoundException")
    void execute_notFound_throwsException() {
        when(reservationRepository.findByReservationId(any(ReservationId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RESERVATION_UUID, ACTOR_ID, "razón"))
                .isInstanceOf(ReservationNotFoundException.class);
    }
}
