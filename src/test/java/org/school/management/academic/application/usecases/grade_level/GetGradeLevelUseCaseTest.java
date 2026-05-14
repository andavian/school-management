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
import org.school.management.academic.domain.exception.GradeLevelNotFoundException;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.valueobject.Division;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.enums.Shift;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetGradeLevelUseCase")
class GetGradeLevelUseCaseTest {

    @Mock private GradeLevelRepository gradeLevelRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetGradeLevelUseCase useCase;

    private static final UUID UUID_VAL = UUID.randomUUID();
    private static final String ID = UUID_VAL.toString();

    @Test
    @DisplayName("execute — flujo feliz — encuentra el curso")
    void execute_happyPath_returnsGradeLevel() {
        GradeLevel gradeLevel = GradeLevel.builder()
                .gradeLevelId(new GradeLevelId(UUID_VAL))
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .yearLevel(YearLevel.of(1))
                .division(Division.of("A"))
                .shift(Shift.MORNING)
                .maxStudents(30)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        GradeLevelResponse response = mock(GradeLevelResponse.class);

        when(gradeLevelRepository.findById(any(GradeLevelId.class))).thenReturn(Optional.of(gradeLevel));
        when(mapper.toGradeLevelResponse(gradeLevel)).thenReturn(response);

        GradeLevelResponse result = useCase.execute(ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — curso no encontrado — lanza GradeLevelNotFoundException")
    void execute_notFound_throwsException() {
        when(gradeLevelRepository.findById(any(GradeLevelId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID))
                .isInstanceOf(GradeLevelNotFoundException.class);
    }
}
