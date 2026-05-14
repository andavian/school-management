package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.SearchPlacesRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SearchPlacesUseCase")
class SearchPlacesUseCaseTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private SearchPlacesUseCase useCase;

    @Test
    @DisplayName("execute — sin provinceId → búsqueda global")
    void execute_noProvinceId_globalSearch() {
        SearchPlacesRequest request = new SearchPlacesRequest("Córdoba", null);

        when(placeRepository.searchByNameWithHierarchy(anyString())).thenReturn(List.of());

        List<PlaceResponse> result = useCase.execute(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — con provinceId → búsqueda en provincia")
    void execute_withProvinceId_provinceSearch() {
        SearchPlacesRequest request = new SearchPlacesRequest("Córdoba", UUID.randomUUID());

        when(placeRepository.searchByNameInProvinceWithHierarchy(anyString(), any())).thenReturn(List.of());

        List<PlaceResponse> result = useCase.execute(request);

        assertThat(result).isEmpty();
    }
}
