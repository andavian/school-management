package org.school.management.academic.application.usecases.grade_level;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.GradeLevelResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.repository.GradeLevelRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListGradeLevelsUseCase")
class ListGradeLevelsUseCaseTest {

    @Mock private GradeLevelRepository gradeLevelRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListGradeLevelsUseCase useCase;

    @Test
    @DisplayName("execute — academicYearId null + activeOnly false → findCurrentYearActiveLevels")
    void execute_nullYearIdActiveOnlyFalse_usesFindCurrentYearActive() {
        when(gradeLevelRepository.findCurrentYearActiveLevels()).thenReturn(Collections.emptyList());

        List<GradeLevelResponse> result = useCase.execute(null, false);

        assertThat(result).isEmpty();
        verify(gradeLevelRepository).findCurrentYearActiveLevels();
    }

    @Test
    @DisplayName("execute — academicYearId dado + activeOnly true → findActiveByAcademicYear")
    void execute_withYearIdAndActiveOnly_usesFindActive() {
        when(gradeLevelRepository.findActiveByAcademicYear(any())).thenReturn(Collections.emptyList());

        List<GradeLevelResponse> result = useCase.execute(
                java.util.UUID.randomUUID().toString(), true);

        assertThat(result).isEmpty();
        verify(gradeLevelRepository).findActiveByAcademicYear(any());
    }

    @Test
    @DisplayName("execute — academicYearId dado + activeOnly false → findByAcademicYear")
    void execute_withYearIdNotActiveOnly_usesFindByAcademicYear() {
        when(gradeLevelRepository.findByAcademicYear(any())).thenReturn(Collections.emptyList());

        List<GradeLevelResponse> result = useCase.execute(
                java.util.UUID.randomUUID().toString(), false);

        assertThat(result).isEmpty();
        verify(gradeLevelRepository).findByAcademicYear(any());
    }
}
