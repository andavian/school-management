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
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListOrientationsUseCase")
class ListOrientationsUseCaseTest {

    @Mock private OrientationRepository orientationRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListOrientationsUseCase useCase;

    @Test
    @DisplayName("execute — activeOnly=true → findActiveOrientations")
    void execute_activeOnly_usesFindActive() {
        when(orientationRepository.findActiveOrientations()).thenReturn(Collections.emptyList());

        List<OrientationResponse> result = useCase.execute(true);

        assertThat(result).isEmpty();
        verify(orientationRepository).findActiveOrientations();
    }

    @Test
    @DisplayName("execute — activeOnly=false → findAll")
    void execute_all_usesFindAll() {
        when(orientationRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrientationResponse> result = useCase.execute(false);

        assertThat(result).isEmpty();
        verify(orientationRepository).findAll();
    }
}
