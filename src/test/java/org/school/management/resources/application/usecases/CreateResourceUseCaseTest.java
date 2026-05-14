package org.school.management.resources.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.resources.application.dto.request.CreateResourceRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateResourceUseCase")
class CreateResourceUseCaseTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private CreateResourceUseCase useCase;

    private static final UUID ACTOR_ID = UUID.randomUUID();

    private CreateResourceRequest buildRequest() {
        return new CreateResourceRequest("PROY-001", "Proyector", ResourceType.PROJECTOR, "", "Aula 1", true, "");
    }

    @Test
    @DisplayName("execute — flujo feliz — crea recurso")
    void execute_happyPath_createsResource() {
        CreateResourceRequest request = buildRequest();

        when(resourceRepository.existsByCode("PROY-001")).thenReturn(false);
        when(resourceRepository.save(any(Resource.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResourceResponse(any(Resource.class))).thenReturn(mock(ResourceResponse.class));

        ResourceResponse result = useCase.execute(request, ACTOR_ID);

        assertThat(result).isNotNull();
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    @DisplayName("execute — código duplicado — lanza IllegalArgumentException")
    void execute_duplicateCode_throwsException() {
        CreateResourceRequest request = buildRequest();

        when(resourceRepository.existsByCode("PROY-001")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request, ACTOR_ID))
                .isInstanceOf(IllegalArgumentException.class);

        verify(resourceRepository, never()).save(any());
    }
}
