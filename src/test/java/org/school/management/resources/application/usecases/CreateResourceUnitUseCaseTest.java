package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.application.dto.request.CreateResourceUnitRequest;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.ResourceNotFoundException;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ResourceId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateResourceUnitUseCase")
class CreateResourceUnitUseCaseTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceUnitRepository resourceUnitRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private CreateResourceUnitUseCase useCase;

    private static final UUID RESOURCE_ID = UUID.randomUUID();
    private static final UUID ACTOR_ID = UUID.randomUUID();

    private CreateResourceUnitRequest buildRequest() {
        return new CreateResourceUnitRequest(RESOURCE_ID, "PROY-001-U1", "SN123", null);
    }

    @Test
    @DisplayName("execute — flujo feliz — crea unidad")
    void execute_happyPath_createsUnit() {
        CreateResourceUnitRequest request = buildRequest();

        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.of(mock(Resource.class)));
        when(resourceUnitRepository.existsByUnitCode("PROY-001-U1")).thenReturn(false);
        when(resourceUnitRepository.save(any(ResourceUnit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResourceUnitResponse(any(ResourceUnit.class))).thenReturn(mock(ResourceUnitResponse.class));

        ResourceUnitResponse result = useCase.execute(request, ACTOR_ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — recurso padre no encontrado — lanza ResourceNotFoundException")
    void execute_parentNotFound_throwsException() {
        CreateResourceUnitRequest request = buildRequest();

        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, ACTOR_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
