package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetResourceAvailabilityUseCase")
class GetResourceAvailabilityUseCaseTest {

    @Mock private ResourceUnitRepository resourceUnitRepository;
    @Mock private ReservationRepository reservationRepository;

    @InjectMocks private GetResourceAvailabilityUseCase useCase;

    private static final UUID RESOURCE_UUID = UUID.randomUUID();
    private static final ResourceId RESOURCE_ID = ResourceId.of(RESOURCE_UUID);

    @Test
    @DisplayName("execute — flujo feliz — calcula disponibilidad")
    void execute_happyPath_returnsAvailability() {
        when(resourceUnitRepository.findByResourceIdAndStatus(eq(RESOURCE_ID), eq(UnitStatus.AVAILABLE)))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.findReservedUnitIdsForDateRange(any(), any(), any(), any()))
                .thenReturn(Collections.emptySet());

        GetResourceAvailabilityUseCase.AvailabilityInfo result = useCase.execute(
                RESOURCE_ID, LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(12, 0));

        assertThat(result).isNotNull();
        assertThat(result.totalAvailable()).isEqualTo(0);
        assertThat(result.freeInRange()).isEqualTo(0);
    }
}
