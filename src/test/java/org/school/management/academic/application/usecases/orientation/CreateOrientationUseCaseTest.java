package org.school.management.academic.application.usecases.orientation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.CreateOrientationRequest;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationAlreadyExistsException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateOrientationUseCase")
class CreateOrientationUseCaseTest {

    @Mock private OrientationRepository orientationRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CreateOrientationUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — crea orientación")
    void execute_happyPath_createsOrientation() {
        CreateOrientationRequest request = new CreateOrientationRequest("Informática", "INFO", "", 4);

        when(orientationRepository.existsByCode("INFO")).thenReturn(false);
        when(orientationRepository.save(any(Orientation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toOrientationResponse(any(Orientation.class))).thenReturn(mock(OrientationResponse.class));

        OrientationResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        verify(orientationRepository).save(any(Orientation.class));
    }

    @Test
    @DisplayName("execute — código duplicado — lanza OrientationAlreadyExistsException")
    void execute_duplicateCode_throwsException() {
        CreateOrientationRequest request = new CreateOrientationRequest("Informática", "INFO", "", 4);

        when(orientationRepository.existsByCode("INFO")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(OrientationAlreadyExistsException.class);

        verify(orientationRepository, never()).save(any());
    }
}
