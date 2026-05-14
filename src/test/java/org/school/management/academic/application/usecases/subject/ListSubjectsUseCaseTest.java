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
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListSubjectsUseCase")
class ListSubjectsUseCaseTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListSubjectsUseCase useCase;

    @Test
    @DisplayName("execute — sin filtros → findAll")
    void execute_noFilters_usesFindAll() {
        when(subjectRepository.findAll()).thenReturn(Collections.emptyList());

        List<SubjectResponse> result = useCase.execute(null, null, null);

        assertThat(result).isEmpty();
        verify(subjectRepository).findAll();
    }

    @Test
    @DisplayName("execute — yearLevel + orientationId → findByYearLevelAndOrientation")
    void execute_yearLevelAndOrientation_usesSpecificFind() {
        when(subjectRepository.findByYearLevelAndOrientation(any(), any())).thenReturn(Collections.emptyList());

        List<SubjectResponse> result = useCase.execute(1, java.util.UUID.randomUUID().toString(), null);

        assertThat(result).isEmpty();
        verify(subjectRepository).findByYearLevelAndOrientation(any(), any());
    }

    @Test
    @DisplayName("execute — activeOnly=true → findActiveSubjects")
    void execute_activeOnly_usesFindActive() {
        when(subjectRepository.findActiveSubjects()).thenReturn(Collections.emptyList());

        List<SubjectResponse> result = useCase.execute(null, null, true);

        assertThat(result).isEmpty();
        verify(subjectRepository).findActiveSubjects();
    }

    @Test
    @DisplayName("execute — solo yearLevel → findByYearLevel")
    void execute_onlyYearLevel_usesFindByYearLevel() {
        when(subjectRepository.findByYearLevel(any())).thenReturn(Collections.emptyList());

        List<SubjectResponse> result = useCase.execute(1, null, null);

        assertThat(result).isEmpty();
        verify(subjectRepository).findByYearLevel(any());
    }
}
