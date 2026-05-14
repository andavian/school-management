package org.school.management.teachers.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.teachers.application.dto.response.TeacherSummaryResponse;
import org.school.management.teachers.application.mapper.TeacherApplicationMapper;
import org.school.management.teachers.domain.repository.TeacherRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SearchTeachersUseCase")
class SearchTeachersUseCaseTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private TeacherApplicationMapper mapper;

    @InjectMocks private SearchTeachersUseCase useCase;

    @Test
    @DisplayName("execute — sin filtros → findAll")
    void execute_noFilters_usesFindAll() {
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        List<TeacherSummaryResponse> result = useCase.execute(null, null);

        assertThat(result).isEmpty();
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("execute — por apellido → findByLastName")
    void execute_byLastName_usesFindByLastName() {
        when(teacherRepository.findByLastName(any())).thenReturn(Collections.emptyList());

        List<TeacherSummaryResponse> result = useCase.execute(null, "Pérez");

        assertThat(result).isEmpty();
        verify(teacherRepository).findByLastName("Pérez");
    }
}
