package org.school.management.course.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.repository.CourseSubjectRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetCourseSubjectsByGradeLevelUseCase")
class GetCourseSubjectsByGradeLevelUseCaseTest {

    @Mock private CourseSubjectRepository courseSubjectRepository;
    @Mock private CourseApplicationMapper mapper;

    @InjectMocks private GetCourseSubjectsByGradeLevelUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista materias del curso")
    void execute_happyPath_listsSubjects() {
        when(courseSubjectRepository.findByGradeLevelAndYear(any(), any())).thenReturn(Collections.emptyList());

        List<CourseSubjectResponse> result = useCase.execute(UUID.randomUUID(), UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}
