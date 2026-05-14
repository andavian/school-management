package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.ListProvincesByCountryRequest;
import org.school.management.geography.application.dto.response.ProvinceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.CountryNotFoundException;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.CountryRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.shared.geography.domain.valueobject.CountryId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListProvincesByCountryUseCase")
class ListProvincesByCountryUseCaseTest {

    @Mock private ProvinceRepository provinceRepository;
    @Mock private CountryRepository countryRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private ListProvincesByCountryUseCase useCase;

    private static final UUID COUNTRY_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — lista provincias del país")
    void execute_happyPath_listsProvinces() {
        ListProvincesByCountryRequest request = new ListProvincesByCountryRequest(COUNTRY_ID);

        when(countryRepository.findById(any(CountryId.class))).thenReturn(Optional.of(mock(Country.class)));
        when(provinceRepository.findByCountryId(any(CountryId.class))).thenReturn(List.of());

        List<ProvinceResponse> result = useCase.execute(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — país no encontrado — lanza CountryNotFoundException")
    void execute_countryNotFound_throwsException() {
        ListProvincesByCountryRequest request = new ListProvincesByCountryRequest(COUNTRY_ID);

        when(countryRepository.findById(any(CountryId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(CountryNotFoundException.class);
    }
}
