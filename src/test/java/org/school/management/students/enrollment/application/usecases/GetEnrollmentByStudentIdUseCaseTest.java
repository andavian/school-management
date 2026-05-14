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
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetEnrollmentByStudentIdUseCase")
class GetEnrollmentByStudentIdUseCaseTest {

    @Mock private StudentEnrollmentRepository enrollmentRepository;
    @Mock private StudentEnrollmentApplicationMapper mapper;

    @InjectMocks private GetEnrollmentByStudentIdUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista inscripciones")
    void execute_happyPath_listsEnrollments() {
        when(enrollmentRepository.findAllByStudentId(any())).thenReturn(Collections.emptyList());

        List<EnrollmentResponse> result = useCase.execute(UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}
