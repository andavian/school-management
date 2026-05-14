package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.CreatePlaceRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.DuplicatePlaceException;
import org.school.management.geography.domain.exception.ProvinceNotFoundException;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.geography.domain.valueobject.ProvinceId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreatePlaceUseCase")
class CreatePlaceUseCaseTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private ProvinceRepository provinceRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private CreatePlaceUseCase useCase;

    private static final UUID PROVINCE_ID = UUID.randomUUID();

    private CreatePlaceRequest buildRequest() {
        return new CreatePlaceRequest("Córdoba", PROVINCE_ID, "CIUDAD", "5000");
    }

    @Test
    @DisplayName("execute — flujo feliz — crea lugar con jerarquía")
    void execute_happyPath_createsPlaceWithHierarchy() {
        CreatePlaceRequest request = buildRequest();

        when(provinceRepository.findById(any(ProvinceId.class)))
                .thenReturn(Optional.of(mock(Province.class)));
        when(placeRepository.existsByNameAndProvince(any(), any())).thenReturn(false);
        when(placeRepository.save(any(Place.class))).thenAnswer(inv -> inv.getArgument(0));
        when(placeRepository.findByIdWithHierarchy(any(PlaceId.class)))
                .thenReturn(Optional.of(mock(PlaceWithHierarchy.class)));
        when(mapper.toPlaceResponse(any(PlaceWithHierarchy.class))).thenReturn(mock(PlaceResponse.class));

        PlaceResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        verify(placeRepository).save(any(Place.class));
    }

    @Test
    @DisplayName("execute — provincia no encontrada — lanza ProvinceNotFoundException")
    void execute_provinceNotFound_throwsException() {
        CreatePlaceRequest request = buildRequest();

        when(provinceRepository.findById(any(ProvinceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ProvinceNotFoundException.class);

        verify(placeRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — lugar duplicado en misma provincia — lanza DuplicatePlaceException")
    void execute_duplicatePlace_throwsException() {
        CreatePlaceRequest request = buildRequest();
        Province province = mock(Province.class);
        when(province.getNameAsString()).thenReturn("Córdoba");

        when(provinceRepository.findById(any(ProvinceId.class))).thenReturn(Optional.of(province));
        when(placeRepository.existsByNameAndProvince(eq("Córdoba"), any(ProvinceId.class))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DuplicatePlaceException.class);

        verify(placeRepository, never()).save(any());
    }
}
