package org.school.management.attendance.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.attendance.application.dto.request.RecordCourseAttendanceRequest;
import org.school.management.attendance.application.dto.response.CourseAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.attendance.domain.exception.AttendanceAlreadyRecordedException;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.domain.repository.CourseAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RecordCourseAttendanceUseCase")
class RecordCourseAttendanceUseCaseTest {

    @Mock private CourseAttendanceRepository courseAttendanceRepository;
    @Mock private AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock private AttendanceApplicationMapper mapper;
    @InjectMocks private RecordCourseAttendanceUseCase useCase;

    private static final UUID SCS_ID    = UUID.randomUUID(); // studentCourseSubjectId
    private static final UUID CS_ID     = UUID.randomUUID(); // courseSubjectId
    private static final UUID PERIOD_ID = UUID.randomUUID();
    private static final UUID USER_ID   = UUID.randomUUID();
    private static final LocalDate TODAY = LocalDate.now();

    // ── helpers ──────────────────────────────────────────────────────────────

    private RecordCourseAttendanceRequest buildRequest(String status) {
        return new RecordCourseAttendanceRequest(SCS_ID, CS_ID, PERIOD_ID, TODAY, status, null);
    }

    private CourseAttendance buildDomain(AttendanceStatus status) {
        return CourseAttendance.create(
                CourseAttendanceId.generate(),
                StudentCourseSubjectId.of(SCS_ID),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID),
                TODAY, status, null, USER_ID);
    }

    private CourseAttendanceResponse buildResponse(String status) {
        return new CourseAttendanceResponse(
                UUID.randomUUID(), SCS_ID, CS_ID, PERIOD_ID,
                TODAY, status, AttendanceStatus.valueOf(status).getAbsenceWeight(),
                null, USER_ID, LocalDateTime.now(), LocalDateTime.now());
    }

    private AttendanceSummary buildSummary() {
        return AttendanceSummary.create(
                AttendanceSummaryId.generate(),
                StudentCourseSubjectId.of(SCS_ID),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID));
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — guarda el registro y crea nuevo summary cuando no existe")
    void execute_whenNoExistingSummary_thenCreatesNewSummary() {
        // given
        RecordCourseAttendanceRequest request = buildRequest("PRESENT");
        CourseAttendance domain = buildDomain(AttendanceStatus.PRESENT);
        CourseAttendanceResponse expected = buildResponse("PRESENT");

        when(courseAttendanceRepository.existsByStudentCourseSubjectIdAndClassDate(
                StudentCourseSubjectId.of(SCS_ID), TODAY)).thenReturn(false);
        when(courseAttendanceRepository.save(any())).thenReturn(domain);
        when(mapper.toCourseAttendanceResponse(domain)).thenReturn(expected);
        when(courseAttendanceRepository.findAllByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(List.of(domain));
        when(attendanceSummaryRepository.findByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(Optional.empty());
        when(attendanceSummaryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        CourseAttendanceResponse result = useCase.execute(request, USER_ID);

        // then
        assertThat(result).isEqualTo(expected);
        verify(attendanceSummaryRepository).save(any(AttendanceSummary.class));
    }

    @Test
    @DisplayName("execute — actualiza summary existente con recalculate()")
    void execute_whenSummaryExists_thenRecalculatesExistingSummary() {
        // given
        RecordCourseAttendanceRequest request = buildRequest("ABSENT");
        CourseAttendance domain = buildDomain(AttendanceStatus.ABSENT);
        AttendanceSummary existingSummary = buildSummary();

        when(courseAttendanceRepository.existsByStudentCourseSubjectIdAndClassDate(any(), any()))
                .thenReturn(false);
        when(courseAttendanceRepository.save(any())).thenReturn(domain);
        when(mapper.toCourseAttendanceResponse(any())).thenReturn(buildResponse("ABSENT"));
        when(courseAttendanceRepository.findAllByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(List.of(domain));
        when(attendanceSummaryRepository.findByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(Optional.of(existingSummary));
        when(attendanceSummaryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        useCase.execute(request, USER_ID);

        // then — capturamos el summary guardado para verificar que recalculó
        ArgumentCaptor<AttendanceSummary> summaryCaptor =
                ArgumentCaptor.forClass(AttendanceSummary.class);
        verify(attendanceSummaryRepository).save(summaryCaptor.capture());

        AttendanceSummary saved = summaryCaptor.getValue();
        assertThat(saved.getTotalClasses()).isEqualTo(1);
        assertThat(saved.getAbsentCount()).isEqualTo(1);
        assertThat(saved.getWeightedAbsences()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("execute — lanza AttendanceAlreadyRecordedException si ya existe para esa clase")
    void execute_whenAlreadyRecorded_thenThrowsAlreadyRecordedException() {
        // given
        RecordCourseAttendanceRequest request = buildRequest("PRESENT");

        when(courseAttendanceRepository.existsByStudentCourseSubjectIdAndClassDate(
                StudentCourseSubjectId.of(SCS_ID), TODAY)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> useCase.execute(request, USER_ID))
                .isInstanceOf(AttendanceAlreadyRecordedException.class)
                .hasMessageContaining(SCS_ID.toString());

        verify(courseAttendanceRepository, never()).save(any());
        verify(attendanceSummaryRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — tardanza (LATE) tiene peso 0.2 en el summary")
    void execute_whenLate_thenSummaryHasWeight02() {
        // given
        RecordCourseAttendanceRequest request = buildRequest("LATE");
        CourseAttendance lateDomain = buildDomain(AttendanceStatus.LATE);

        // AGREGAMOS UNA CLASE PRESENTE PARA DILUIR EL RIESGO
        CourseAttendance presentDomain = buildDomain(AttendanceStatus.PRESENT);

        when(courseAttendanceRepository.existsByStudentCourseSubjectIdAndClassDate(any(), any()))
                .thenReturn(false);
        when(courseAttendanceRepository.save(any())).thenReturn(lateDomain);
        when(mapper.toCourseAttendanceResponse(any())).thenReturn(buildResponse("LATE"));

        // CONFIGURAMOS EL MOCK PARA QUE DEVUELVA 2 CLASES
        when(courseAttendanceRepository.findAllByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(List.of(lateDomain, presentDomain));

        when(attendanceSummaryRepository.findByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(Optional.of(buildSummary()));
        when(attendanceSummaryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        useCase.execute(request, USER_ID);

        // then
        ArgumentCaptor<AttendanceSummary> captor = ArgumentCaptor.forClass(AttendanceSummary.class);
        verify(attendanceSummaryRepository).save(captor.capture());

        AttendanceSummary saved = captor.getValue();

        // Verificaciones
        assertThat(saved.getLateCount()).isEqualTo(1);
        assertThat(saved.getWeightedAbsences()).isEqualTo(0.2);

        // Ratio: 0.2 / 2 = 0.1 (10%). 10% < 15%, por lo tanto:
        assertThat(saved.isAtRisk()).isFalse();
    }

    @Test
    @DisplayName("execute — lanza IllegalArgumentException si status es inválido")
    void execute_whenInvalidStatus_thenThrowsIllegalArgumentException() {
        // given
        RecordCourseAttendanceRequest request = buildRequest("WRONG");


        // when / then
        assertThatThrownBy(() -> useCase.execute(request, USER_ID))
                .isInstanceOf(IllegalArgumentException.class);

        verify(courseAttendanceRepository, never()).save(any());
    }
}