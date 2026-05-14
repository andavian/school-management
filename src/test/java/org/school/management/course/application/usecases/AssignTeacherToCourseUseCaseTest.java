package org.school.management.course.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.course.application.dto.request.AssignTeacherRequest;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.exception.CourseSubjectNotFoundException;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("AssignTeacherToCourseUseCase")
class AssignTeacherToCourseUseCaseTest {

    @Mock private CourseSubjectRepository courseSubjectRepository;
    @Mock private CourseApplicationMapper mapper;

    @InjectMocks private AssignTeacherToCourseUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — asigna profesor al curso")
    void execute_happyPath_assignsTeacher() {
        UUID courseSubjectId = UUID.randomUUID();
        AssignTeacherRequest request = new AssignTeacherRequest(UUID.randomUUID());
        CourseSubject courseSubject = mock(CourseSubject.class);

        when(courseSubjectRepository.findById(any())).thenReturn(Optional.of(courseSubject));
        doNothing().when(courseSubject).assignTeacher(any());
        when(courseSubjectRepository.save(any(CourseSubject.class))).thenReturn(courseSubject);
        when(mapper.toResponse(any(CourseSubject.class))).thenReturn(mock(CourseSubjectResponse.class));

        CourseSubjectResponse result = useCase.execute(courseSubjectId, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — curso no encontrado — lanza CourseSubjectNotFoundException")
    void execute_notFound_throwsException() {
        AssignTeacherRequest request = new AssignTeacherRequest(UUID.randomUUID());

        when(courseSubjectRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), request))
                .isInstanceOf(CourseSubjectNotFoundException.class);
    }
}
