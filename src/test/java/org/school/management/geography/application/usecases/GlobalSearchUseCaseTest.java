package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.GlobalSearchRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GlobalSearchUseCase")
class GlobalSearchUseCaseTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private GlobalSearchUseCase useCase;

    @Test
    @DisplayName("execute — búsqueda sin límite — retorna todos los resultados")
    void execute_noMaxResults_returnsAll() {
        List<PlaceWithHierarchy> results = List.of(mock(PlaceWithHierarchy.class));
        when(placeRepository.searchByNameWithHierarchy(anyString())).thenReturn(results);
        when(mapper.toPlaceResponse(any(PlaceWithHierarchy.class))).thenReturn(mock(PlaceResponse.class));

        List<PlaceResponse> result = useCase.execute(new GlobalSearchRequest("Córdoba", null));

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("execute — búsqueda con límite — trunca resultados")
    void execute_withMaxResults_truncates() {
        List<PlaceWithHierarchy> results = List.of(
                mock(PlaceWithHierarchy.class), mock(PlaceWithHierarchy.class), mock(PlaceWithHierarchy.class));
        when(placeRepository.searchByNameWithHierarchy(anyString())).thenReturn(results);
        when(mapper.toPlaceResponse(any(PlaceWithHierarchy.class))).thenReturn(mock(PlaceResponse.class));

        List<PlaceResponse> result = useCase.execute(new GlobalSearchRequest("Córdoba", 2));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("execute — sin resultados — retorna lista vacía")
    void execute_noResults_returnsEmpty() {
        when(placeRepository.searchByNameWithHierarchy(anyString())).thenReturn(List.of());

        List<PlaceResponse> result = useCase.execute(new GlobalSearchRequest("ZZZ", null));

        assertThat(result).isEmpty();
    }
}
