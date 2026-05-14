package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.application.dto.request.UpdateUnitStatusRequest;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateUnitStatusUseCase")
class UpdateUnitStatusUseCaseTest {

    @Mock private ResourceUnitRepository resourceUnitRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private UpdateUnitStatusUseCase useCase;

    private static final UUID UNIT_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — cambia estado de unidad")
    void execute_happyPath_updatesUnitStatus() {
        UpdateUnitStatusRequest request = new UpdateUnitStatusRequest(UnitStatus.MAINTENANCE, null, null);
        ResourceUnit unit = mock(ResourceUnit.class);
        when(unit.getUnitStatus()).thenReturn(UnitStatus.AVAILABLE, UnitStatus.MAINTENANCE);

        when(resourceUnitRepository.findByUnitId(any(UnitId.class))).thenReturn(Optional.of(unit));
        when(resourceUnitRepository.save(any(ResourceUnit.class))).thenReturn(unit);
        when(mapper.toResourceUnitResponse(any(ResourceUnit.class))).thenReturn(mock(ResourceUnitResponse.class));

        ResourceUnitResponse result = useCase.execute(UNIT_ID, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — unidad no encontrada — lanza IllegalArgumentException")
    void execute_notFound_throwsException() {
        UpdateUnitStatusRequest request = new UpdateUnitStatusRequest(UnitStatus.MAINTENANCE, null, null);

        when(resourceUnitRepository.findByUnitId(any(UnitId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UNIT_ID, request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
