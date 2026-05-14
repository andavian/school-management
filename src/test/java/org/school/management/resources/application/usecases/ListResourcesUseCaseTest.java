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
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListResourcesUseCase")
class ListResourcesUseCaseTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceApplicationMapper mapper;

    @InjectMocks private ListResourcesUseCase useCase;

    @Test
    @DisplayName("execute — solo reservables → findAllActiveAndReservable")
    void execute_reservableOnly_usesFindReservable() {
        when(resourceRepository.findAllActiveAndReservable()).thenReturn(List.of());

        List<ResourceResponse> result = useCase.execute(null, true);

        assertThat(result).isEmpty();
        verify(resourceRepository).findAllActiveAndReservable();
    }

    @Test
    @DisplayName("execute — todos → findAllActive")
    void execute_all_usesFindAllActive() {
        when(resourceRepository.findAllActive()).thenReturn(List.of());

        List<ResourceResponse> result = useCase.execute(null, false);

        assertThat(result).isEmpty();
        verify(resourceRepository).findAllActive();
    }

    @Test
    @DisplayName("execute — con tipo → filtra por tipo")
    void execute_withType_filtersByType() {
        Resource resource = mock(Resource.class);
        when(resource.getResourceType()).thenReturn(ResourceType.PROJECTOR);
        when(resourceRepository.findAllActive()).thenReturn(List.of(resource));
        when(mapper.toResourceResponse(any(Resource.class))).thenReturn(mock(ResourceResponse.class));

        List<ResourceResponse> result = useCase.execute(ResourceType.PROJECTOR, false);

        assertThat(result).hasSize(1);
    }
}
