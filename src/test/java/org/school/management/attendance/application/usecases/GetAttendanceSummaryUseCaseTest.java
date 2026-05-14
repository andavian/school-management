package org.school.management.attendance.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.attendance.application.dto.response.AttendanceSummaryResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetAttendanceSummaryUseCase")
class GetAttendanceSummaryUseCaseTest {

    @Mock private AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock private AttendanceApplicationMapper mapper;

    @InjectMocks private GetAttendanceSummaryUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra resumen")
    void execute_happyPath_returnsSummary() {
        when(attendanceSummaryRepository.findByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(Optional.of(mock(AttendanceSummary.class)));
        when(mapper.toAttendanceSummaryResponse(any())).thenReturn(mock(AttendanceSummaryResponse.class));

        AttendanceSummaryResponse result = useCase.execute(UUID.randomUUID(), UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza AttendanceNotFoundException")
    void execute_notFound_throwsException() {
        when(attendanceSummaryRepository.findByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(AttendanceNotFoundException.class);
    }
}
