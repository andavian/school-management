package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.response.GeographyStatisticsResponse;
import org.school.management.geography.domain.repository.CountryRepository;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetGeographyStatisticsUseCase")
class GetGeographyStatisticsUseCaseTest {

    @Mock private CountryRepository countryRepository;
    @Mock private ProvinceRepository provinceRepository;
    @Mock private PlaceRepository placeRepository;

    @InjectMocks private GetGeographyStatisticsUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — retorna estadísticas")
    void execute_happyPath_returnsStatistics() {
        when(countryRepository.count()).thenReturn(195L);
        when(provinceRepository.findAll()).thenReturn(Collections.emptyList());
        when(placeRepository.findAll()).thenReturn(Collections.emptyList());
        when(placeRepository.countByType(any())).thenReturn(0L);

        GeographyStatisticsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.totalCountries()).isEqualTo(195);
    }
}
