package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
@DisplayName("GetResourceByIdUseCase")
class GetResourceByIdUseCaseTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private GetResourceByIdUseCase useCase;

    private static final UUID RESOURCE_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — encuentra recurso")
    void execute_happyPath_returnsResource() {
        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.of(mock(Resource.class)));
        when(mapper.toResourceResponse(any(Resource.class))).thenReturn(mock(ResourceResponse.class));

        ResourceResponse result = useCase.execute(RESOURCE_ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza ResourceNotFoundException")
    void execute_notFound_throwsException() {
        when(resourceRepository.findByResourceId(any(ResourceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RESOURCE_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
