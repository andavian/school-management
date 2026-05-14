package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.GetCountryByIsoCodeRequest;
import org.school.management.geography.application.dto.response.CountryResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.CountryNotFoundException;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.repository.CountryRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetCountryByIsoCodeUseCase")
class GetCountryByIsoCodeUseCaseTest {

    @Mock private CountryRepository countryRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private GetCountryByIsoCodeUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra país por código ISO")
    void execute_happyPath_returnsCountry() {
        when(countryRepository.findByIsoCode(anyString())).thenReturn(Optional.of(mock(Country.class)));
        when(mapper.toCountryResponse(any(Country.class))).thenReturn(mock(CountryResponse.class));

        CountryResponse result = useCase.execute(new GetCountryByIsoCodeRequest("AR"));

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — país no encontrado — lanza CountryNotFoundException")
    void execute_notFound_throwsException() {
        when(countryRepository.findByIsoCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new GetCountryByIsoCodeRequest("XX")))
                .isInstanceOf(CountryNotFoundException.class);
    }
}
