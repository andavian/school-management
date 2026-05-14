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
@DisplayName("AssignHomeroomTeacherUseCase")
class AssignHomeroomTeacherUseCaseTest {

    @Mock private GradeLevelRepository gradeLevelRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private AssignHomeroomTeacherUseCase useCase;

    private static final UUID GRADE_LEVEL_UUID = UUID.randomUUID();
    private static final String GRADE_LEVEL_ID = GRADE_LEVEL_UUID.toString();
    private static final String TEACHER_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — asigna profesor tutor")
    void execute_happyPath_assignsHomeroomTeacher() {
        GradeLevel gradeLevel = GradeLevel.builder()
                .gradeLevelId(new GradeLevelId(GRADE_LEVEL_UUID))
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .yearLevel(YearLevel.of(1))
                .division(Division.of("A"))
                .shift(Shift.MORNING)
                .maxStudents(30)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gradeLevelRepository.findById(any(GradeLevelId.class))).thenReturn(Optional.of(gradeLevel));
        when(gradeLevelRepository.save(any(GradeLevel.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toGradeLevelResponse(any(GradeLevel.class))).thenReturn(mock(GradeLevelResponse.class));

        GradeLevelResponse result = useCase.execute(GRADE_LEVEL_ID, TEACHER_ID);

        assertThat(result).isNotNull();
        verify(gradeLevelRepository).save(any(GradeLevel.class));
    }

    @Test
    @DisplayName("execute — curso no encontrado — lanza GradeLevelNotFoundException")
    void execute_notFound_throwsException() {
        when(gradeLevelRepository.findById(any(GradeLevelId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(GRADE_LEVEL_ID, TEACHER_ID))
                .isInstanceOf(GradeLevelNotFoundException.class);

        verify(gradeLevelRepository, never()).save(any());
    }
}
