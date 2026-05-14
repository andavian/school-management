package org.school.management.students.enrollment.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.enrollment.application.dto.request.UpdateEnrollmentRequest;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.application.mapper.StudentEnrollmentApplicationMapper;
import org.school.management.students.enrollment.domain.exception.EnrollmentNotFoundException;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateEnrollmentUseCase")
class UpdateEnrollmentUseCaseTest {

    @Mock private StudentEnrollmentRepository enrollmentRepository;
    @Mock private StudentEnrollmentApplicationMapper mapper;

    @InjectMocks private UpdateEnrollmentUseCase useCase;

    private static final UUID ENROLLMENT_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — completa inscripción (finalAverage)")
    void execute_complete_updatesEnrollment() {
        UpdateEnrollmentRequest request = new UpdateEnrollmentRequest(new BigDecimal("8.5"), true, null, null);
        StudentEnrollment enrollment = mock(StudentEnrollment.class);

        when(enrollmentRepository.findByEnrollmentId(any())).thenReturn(Optional.of(enrollment));
        doNothing().when(enrollment).complete(any(BigDecimal.class), anyBoolean());
        when(enrollmentRepository.save(any(StudentEnrollment.class))).thenReturn(enrollment);
        when(mapper.toEnrollmentResponse(any(StudentEnrollment.class))).thenReturn(mock(EnrollmentResponse.class));

        EnrollmentResponse result = useCase.execute(ENROLLMENT_ID, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — baja — retira inscripción (withdrawalReasonId)")
    void execute_withdraw_updatesEnrollment() {
        UpdateEnrollmentRequest request = new UpdateEnrollmentRequest(null, null, UUID.randomUUID(), "Motivo de baja");
        StudentEnrollment enrollment = mock(StudentEnrollment.class);

        when(enrollmentRepository.findByEnrollmentId(any())).thenReturn(Optional.of(enrollment));
        doNothing().when(enrollment).withdraw(any(), any());
        when(enrollmentRepository.save(any(StudentEnrollment.class))).thenReturn(enrollment);
        when(mapper.toEnrollmentResponse(any(StudentEnrollment.class))).thenReturn(mock(EnrollmentResponse.class));

        EnrollmentResponse result = useCase.execute(ENROLLMENT_ID, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrada — lanza EnrollmentNotFoundException")
    void execute_notFound_throwsException() {
        UpdateEnrollmentRequest request = new UpdateEnrollmentRequest(new BigDecimal("8.5"), true, null, null);

        when(enrollmentRepository.findByEnrollmentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ENROLLMENT_ID, request))
                .isInstanceOf(EnrollmentNotFoundException.class);
    }
}
