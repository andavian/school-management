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
import org.school.management.academic.domain.repository.OrientationRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListOrientationsForYearLevelUseCase")
class ListOrientationsForYearLevelUseCaseTest {

    @Mock private OrientationRepository orientationRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListOrientationsForYearLevelUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista orientaciones para año 4")
    void execute_happyPath_listsOrientations() {
        when(orientationRepository.findByAvailableFromYear(4)).thenReturn(Collections.emptyList());

        List<OrientationResponse> result = useCase.execute(4);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — año < 4 — lanza IllegalArgumentException")
    void execute_yearLevelBelow4_throwsException() {
        assertThatThrownBy(() -> useCase.execute(3))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(orientationRepository);
    }

    @Test
    @DisplayName("execute — año > 7 — lanza IllegalArgumentException")
    void execute_yearLevelAbove7_throwsException() {
        assertThatThrownBy(() -> useCase.execute(8))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(orientationRepository);
    }
}
