package org.school.management.academic.application.usecases.orientation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationNotFoundException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.valueobject.ids.OrientationId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetOrientationUseCase")
class GetOrientationUseCaseTest {

    @Mock private OrientationRepository orientationRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetOrientationUseCase useCase;

    private static final String ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — encuentra la orientación")
    void execute_happyPath_returnsOrientation() {
        when(orientationRepository.findById(any(OrientationId.class))).thenReturn(Optional.of(mock(Orientation.class)));
        when(mapper.toOrientationResponse(any(Orientation.class))).thenReturn(mock(OrientationResponse.class));

        OrientationResponse result = useCase.execute(ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrada — lanza OrientationNotFoundException")
    void execute_notFound_throwsException() {
        when(orientationRepository.findById(any(OrientationId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID))
                .isInstanceOf(OrientationNotFoundException.class);
    }
}
