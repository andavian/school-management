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
import org.school.management.resources.domain.repository.ReservationRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetMyReservationsUseCase")
class GetMyReservationsUseCaseTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private GetMyReservationsUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista reservas del usuario")
    void execute_happyPath_listsReservations() {
        when(reservationRepository.findByRequesterId(any(UUID.class))).thenReturn(Collections.emptyList());

        List<ReservationResponse> result = useCase.execute(UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}
