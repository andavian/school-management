package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.application.dto.request.CreateReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateReservationUseCase")
class CreateReservationUseCaseTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceUnitRepository resourceUnitRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private CreateReservationUseCase useCase;

    private static final UUID RESOURCE_ID = UUID.randomUUID();
    private static final UUID REQUESTER_ID = UUID.randomUUID();

    private CreateReservationRequest buildRequest() {
        return new CreateReservationRequest(RESOURCE_ID,
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                1, "Clase de prueba", null);
    }

    @Test
    @DisplayName("execute — flujo feliz — crea reserva con unidades disponibles")
    void execute_happyPath_createsReservation() {
        CreateReservationRequest request = buildRequest();
        Resource resource = mock(Resource.class);
        when(resource.isReservable()).thenReturn(true);
        when(resource.getResourceId()).thenReturn(ResourceId.of(RESOURCE_ID));
        when(resource.getCode()).thenReturn("PROY-001");

        ResourceUnit unit = mock(ResourceUnit.class);
        when(unit.getUnitId()).thenReturn(UnitId.generate());
        when(unit.getResourceId()).thenReturn(ResourceId.of(RESOURCE_ID));
        doNothing().when(unit).assignToReservation();

        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.of(resource));
        when(reservationRepository.findReservedUnitIdsForDateRange(any(), any(), any(), any()))
                .thenReturn(Collections.emptySet());
        when(resourceUnitRepository.findByResourceIdAndStatus(any(), any()))
                .thenReturn(List.of(unit));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toReservationResponse(any(Reservation.class))).thenReturn(mock(ReservationResponse.class));

        ReservationResponse result = useCase.execute(request, REQUESTER_ID, "Juan");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — recurso no encontrado — lanza ResourceNotFoundException")
    void execute_resourceNotFound_throwsException() {
        CreateReservationRequest request = buildRequest();

        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, REQUESTER_ID, "Juan"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("execute — recurso no reservable — lanza IllegalStateException")
    void execute_notReservable_throwsException() {
        CreateReservationRequest request = buildRequest();
        Resource resource = mock(Resource.class);
        when(resource.isReservable()).thenReturn(false);

        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.of(resource));

        assertThatThrownBy(() -> useCase.execute(request, REQUESTER_ID, "Juan"))
                .isInstanceOf(IllegalStateException.class);
    }
}
