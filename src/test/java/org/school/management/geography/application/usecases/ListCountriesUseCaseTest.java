package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.response.CountryResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.repository.CountryRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListCountriesUseCase")
class ListCountriesUseCaseTest {

    @Mock private CountryRepository countryRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private ListCountriesUseCase useCase;

    @Test
    @DisplayName("execute — lista todos los países")
    void execute_listsAllCountries() {
        Country country = mock(Country.class);
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(mapper.toCountryResponse(any(Country.class))).thenReturn(mock(CountryResponse.class));

        List<CountryResponse> result = useCase.execute();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("execute — lista vacía")
    void execute_emptyList_returnsEmpty() {
        when(countryRepository.findAll()).thenReturn(List.of());

        List<CountryResponse> result = useCase.execute();

        assertThat(result).isEmpty();
    }
}
