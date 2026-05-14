package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.ListPlacesByProvinceRequest;
import org.school.management.geography.application.dto.response.PlaceSummaryResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.ProvinceNotFoundException;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.shared.geography.domain.valueobject.ProvinceId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListPlacesByProvinceUseCase")
class ListPlacesByProvinceUseCaseTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private ProvinceRepository provinceRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private ListPlacesByProvinceUseCase useCase;

    private static final UUID PROVINCE_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — sin filtro de tipo — lista todos los lugares")
    void execute_noType_listsAllPlaces() {
        ListPlacesByProvinceRequest request = new ListPlacesByProvinceRequest(PROVINCE_ID, null);

        when(provinceRepository.findById(any(ProvinceId.class))).thenReturn(Optional.of(mock(Province.class)));
        when(placeRepository.findByProvinceId(any(ProvinceId.class))).thenReturn(List.of());

        List<PlaceSummaryResponse> result = useCase.execute(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — con filtro de tipo — filtra por tipo")
    void execute_withType_filtersByType() {
        ListPlacesByProvinceRequest request = new ListPlacesByProvinceRequest(PROVINCE_ID, "CIUDAD");

        when(provinceRepository.findById(any(ProvinceId.class))).thenReturn(Optional.of(mock(Province.class)));
        when(placeRepository.findByProvinceIdAndType(any(ProvinceId.class), any())).thenReturn(List.of());

        List<PlaceSummaryResponse> result = useCase.execute(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — provincia no encontrada — lanza ProvinceNotFoundException")
    void execute_provinceNotFound_throwsException() {
        ListPlacesByProvinceRequest request = new ListPlacesByProvinceRequest(PROVINCE_ID, null);

        when(provinceRepository.findById(any(ProvinceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ProvinceNotFoundException.class);
    }
}
