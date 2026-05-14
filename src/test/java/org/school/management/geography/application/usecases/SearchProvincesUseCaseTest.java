package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.SearchProvincesRequest;
import org.school.management.geography.application.dto.response.ProvinceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.ProvinceRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SearchProvincesUseCase")
class SearchProvincesUseCaseTest {

    @Mock private ProvinceRepository provinceRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private SearchProvincesUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — busca provincias por nombre")
    void execute_happyPath_searchesProvinces() {
        Province province = mock(Province.class);
        when(provinceRepository.searchByName(anyString())).thenReturn(List.of(province));
        when(mapper.toProvinceResponse(any(Province.class))).thenReturn(mock(ProvinceResponse.class));

        List<ProvinceResponse> result = useCase.execute(new SearchProvincesRequest("Córdoba"));

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("execute — sin resultados — retorna lista vacía")
    void execute_noResults_returnsEmpty() {
        when(provinceRepository.searchByName(anyString())).thenReturn(List.of());

        List<ProvinceResponse> result = useCase.execute(new SearchProvincesRequest("ZZZ"));

        assertThat(result).isEmpty();
    }
}
