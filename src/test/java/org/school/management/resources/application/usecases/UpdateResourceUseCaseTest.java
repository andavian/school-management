package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.application.dto.request.UpdateResourceRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.ResourceNotFoundException;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateResourceUseCase")
class UpdateResourceUseCaseTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private UpdateResourceUseCase useCase;

    private static final UUID RESOURCE_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — actualiza recurso")
    void execute_happyPath_updatesResource() {
        UpdateResourceRequest request = new UpdateResourceRequest("Nuevo nombre", "", "", null, "");
        Resource resource = mock(Resource.class);

        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(mapper.toResourceResponse(any(Resource.class))).thenReturn(mock(ResourceResponse.class));

        ResourceResponse result = useCase.execute(RESOURCE_ID, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza ResourceNotFoundException")
    void execute_notFound_throwsException() {
        UpdateResourceRequest request = new UpdateResourceRequest("Nuevo", "", "", null, "");
        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RESOURCE_ID, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
