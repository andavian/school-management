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
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetAtRiskStudentsUseCase")
class GetAtRiskStudentsUseCaseTest {

    @Mock private AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock private AttendanceApplicationMapper mapper;
    @InjectMocks private GetAtRiskStudentsUseCase useCase;

    private static final UUID CS_ID     = UUID.randomUUID();
    private static final UUID PERIOD_ID = UUID.randomUUID();

    // ── helpers ──────────────────────────────────────────────────────────────

    private AttendanceSummary buildAtRiskSummary() {
        AttendanceSummary summary = AttendanceSummary.create(
                AttendanceSummaryId.generate(),
                StudentCourseSubjectId.of(UUID.randomUUID()),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID));
        // Simular 10 clases, 2 ausentes (20% > 15%) → at risk
        summary.recalculate(List.of(
                buildAbsent(), buildAbsent(),
                buildPresent(), buildPresent(), buildPresent(),
                buildPresent(), buildPresent(), buildPresent(),
                buildPresent(), buildPresent()
        ));
        return summary;
    }

    // Stubs mínimos de CourseAttendance para alimentar recalculate()
    private org.school.management.attendance.domain.model.CourseAttendance buildAbsent() {
        return org.school.management.attendance.domain.model.CourseAttendance.create(
                org.school.management.attendance.domain.valueobject.CourseAttendanceId.generate(),
                StudentCourseSubjectId.of(UUID.randomUUID()),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID),
                java.time.LocalDate.now(),
                org.school.management.attendance.domain.valueobject.AttendanceStatus.ABSENT,
                null, UUID.randomUUID());
    }

    private org.school.management.attendance.domain.model.CourseAttendance buildPresent() {
        return org.school.management.attendance.domain.model.CourseAttendance.create(
                org.school.management.attendance.domain.valueobject.CourseAttendanceId.generate(),
                StudentCourseSubjectId.of(UUID.randomUUID()),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID),
                java.time.LocalDate.now(),
                org.school.management.attendance.domain.valueobject.AttendanceStatus.PRESENT,
                null, UUID.randomUUID());
    }

    private AttendanceSummaryResponse buildSummaryResponse(UUID scsId) {
        return new AttendanceSummaryResponse(
                UUID.randomUUID(), scsId, CS_ID, PERIOD_ID,
                10, 8, 2, 0, 0, 0,
                2.0, 80.0, true);
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — retorna lista de alumnos en riesgo mapeados")
    void execute_whenAtRiskStudentsExist_thenReturnsMappedList() {
        // given
        AttendanceSummary atRisk = buildAtRiskSummary();
        UUID scsId = atRisk.getStudentCourseSubjectId().value();
        AttendanceSummaryResponse response = buildSummaryResponse(scsId);

        when(attendanceSummaryRepository.findAtRiskByCourseSubjectIdAndPeriodId(
                CourseSubjectId.of(CS_ID), PeriodId.of(PERIOD_ID)))
                .thenReturn(List.of(atRisk));
        when(mapper.toAttendanceSummaryResponse(atRisk)).thenReturn(response);

        // when
        List<AttendanceSummaryResponse> result = useCase.execute(CS_ID, PERIOD_ID);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).atRisk()).isTrue();
        assertThat(result.get(0).attendancePercentage()).isEqualTo(80.0);
    }

    @Test
    @DisplayName("execute — retorna lista vacía si no hay alumnos en riesgo")
    void execute_whenNoAtRiskStudents_thenReturnsEmptyList() {
        // given
        when(attendanceSummaryRepository.findAtRiskByCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(List.of());

        // when
        List<AttendanceSummaryResponse> result = useCase.execute(CS_ID, PERIOD_ID);

        // then
        assertThat(result).isEmpty();
        verify(mapper, never()).toAttendanceSummaryResponse(any());
    }

    @Test
    @DisplayName("execute — retorna múltiples alumnos en riesgo")
    void execute_whenMultipleAtRiskStudents_thenReturnsAll() {
        // given
        AttendanceSummary s1 = buildAtRiskSummary();
        AttendanceSummary s2 = buildAtRiskSummary();

        when(attendanceSummaryRepository.findAtRiskByCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(List.of(s1, s2));
        when(mapper.toAttendanceSummaryResponse(s1)).thenReturn(buildSummaryResponse(UUID.randomUUID()));
        when(mapper.toAttendanceSummaryResponse(s2)).thenReturn(buildSummaryResponse(UUID.randomUUID()));

        // when
        List<AttendanceSummaryResponse> result = useCase.execute(CS_ID, PERIOD_ID);

        // then
        assertThat(result).hasSize(2);
    }
}