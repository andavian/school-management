package org.school.management.geography.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.request.GetPlaceByIdRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.PlaceNotFoundException;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.shared.geography.domain.valueobject.PlaceId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetPlaceByIdUseCase")
class GetPlaceByIdUseCaseTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private GeographyApplicationMapper mapper;

    @InjectMocks private GetPlaceByIdUseCase useCase;

    private static final UUID PLACE_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — encuentra lugar con jerarquía")
    void execute_happyPath_returnsPlaceWithHierarchy() {
        GetPlaceByIdRequest request = new GetPlaceByIdRequest(PLACE_ID);

        when(placeRepository.findByIdWithHierarchy(any(PlaceId.class)))
                .thenReturn(Optional.of(mock(PlaceWithHierarchy.class)));
        when(mapper.toPlaceResponse(any(PlaceWithHierarchy.class))).thenReturn(mock(PlaceResponse.class));

        PlaceResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — lugar no encontrado — lanza PlaceNotFoundException")
    void execute_notFound_throwsException() {
        GetPlaceByIdRequest request = new GetPlaceByIdRequest(PLACE_ID);

        when(placeRepository.findByIdWithHierarchy(any(PlaceId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(PlaceNotFoundException.class);
    }
}
