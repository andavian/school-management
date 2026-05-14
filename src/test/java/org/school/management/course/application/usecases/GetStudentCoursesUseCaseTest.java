package org.school.management.course.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.course.application.dto.response.StudentCourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.repository.StudentCourseSubjectRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetStudentCoursesUseCase")
class GetStudentCoursesUseCaseTest {

    @Mock private StudentCourseSubjectRepository studentCourseSubjectRepository;
    @Mock private CourseApplicationMapper mapper;

    @InjectMocks private GetStudentCoursesUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista cursos del estudiante")
    void execute_happyPath_listsCourses() {
        when(studentCourseSubjectRepository.findByEnrollment(any())).thenReturn(Collections.emptyList());

        List<StudentCourseSubjectResponse> result = useCase.execute(UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}
