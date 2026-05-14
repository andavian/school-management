package org.school.management.academic.application.usecases.subject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.GradeLevelNotFoundException;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListSubjectsForGradeLevelUseCase")
class ListSubjectsForGradeLevelUseCaseTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private GradeLevelRepository gradeLevelRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListSubjectsForGradeLevelUseCase useCase;

    private static final String GRADE_LEVEL_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — lista materias disponibles para el curso")
    void execute_happyPath_listsSubjects() {
        GradeLevel gradeLevel = mock(GradeLevel.class);
        when(gradeLevelRepository.findById(any(GradeLevelId.class))).thenReturn(Optional.of(gradeLevel));
        when(subjectRepository.findAvailableForGradeLevel(any(), any())).thenReturn(Collections.emptyList());

        List<SubjectResponse> result = useCase.execute(GRADE_LEVEL_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — curso no encontrado — lanza GradeLevelNotFoundException")
    void execute_gradeLevelNotFound_throwsException() {
        when(gradeLevelRepository.findById(any(GradeLevelId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(GRADE_LEVEL_ID))
                .isInstanceOf(GradeLevelNotFoundException.class);
    }
}
