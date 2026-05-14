package org.school.management.students.enrollment.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.application.mapper.StudentEnrollmentApplicationMapper;
import org.school.management.students.enrollment.domain.exception.EnrollmentNotFoundException;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetActiveEnrollmentUseCase")
class GetActiveEnrollmentUseCaseTest {

    @Mock private StudentEnrollmentRepository enrollmentRepository;
    @Mock private StudentEnrollmentApplicationMapper mapper;

    @InjectMocks private GetActiveEnrollmentUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra inscripción activa")
    void execute_happyPath_returnsEnrollment() {
        when(enrollmentRepository.findByStudentIdAndAcademicYearId(any(), any()))
                .thenReturn(Optional.of(mock(StudentEnrollment.class)));
        when(mapper.toEnrollmentResponse(any())).thenReturn(mock(EnrollmentResponse.class));

        EnrollmentResponse result = useCase.execute(UUID.randomUUID(), UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrada — lanza EnrollmentNotFoundException")
    void execute_notFound_throwsException() {
        when(enrollmentRepository.findByStudentIdAndAcademicYearId(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(EnrollmentNotFoundException.class);
    }
}
