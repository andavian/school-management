package org.school.management.attendance.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.attendance.application.dto.request.CorrectAttendanceRequest;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.domain.repository.CourseAttendanceRepository;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CorrectAttendanceUseCase")
class CorrectAttendanceUseCaseTest {

    @Mock private DailyAttendanceRepository dailyAttendanceRepository;
    @Mock private CourseAttendanceRepository courseAttendanceRepository;
    @Mock private AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock private AttendanceApplicationMapper mapper;
    @InjectMocks private CorrectAttendanceUseCase useCase;

    private static final UUID DAILY_ID  = UUID.randomUUID();
    private static final UUID COURSE_ID = UUID.randomUUID();
    private static final UUID USER_ID   = UUID.randomUUID();

    // ── helpers ──────────────────────────────────────────────────────────────

    private DailyAttendance buildDailyDomain(AttendanceStatus status) {
        return DailyAttendance.create(
                DailyAttendanceId.of(DAILY_ID),
                StudentPersonalDataId.of(UUID.randomUUID()),
                GradeLevelId.of(UUID.randomUUID()),
                AcademicYearId.of(UUID.randomUUID()),
                LocalDate.now(), status, null, UUID.randomUUID());
    }

    private CourseAttendance buildCourseDomain(AttendanceStatus status) {
        return CourseAttendance.create(
                CourseAttendanceId.of(COURSE_ID),
                StudentCourseSubjectId.of(UUID.randomUUID()),
                CourseSubjectId.of(UUID.randomUUID()),
                PeriodId.of(UUID.randomUUID()),
                LocalDate.now(), status, null, UUID.randomUUID());
    }

    // ── daily tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("correctDaily — cambia ABSENT a PRESENT y guarda")
    void correctDaily_whenAbsent_thenCorrectedToPresent() {
        // given
        DailyAttendance domain = buildDailyDomain(AttendanceStatus.ABSENT);
        CorrectAttendanceRequest request = new CorrectAttendanceRequest("PRESENT", "Error de carga");

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(DAILY_ID)))
                .thenReturn(Optional.of(domain));
        when(dailyAttendanceRepository.save(domain)).thenReturn(domain);
        when(mapper.toDailyAttendanceResponse(any())).thenReturn(null);

        // when
        useCase.correctDaily(DAILY_ID, request, USER_ID);

        // then
        assertThat(domain.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(domain.getCorrectedByUserId()).isEqualTo(USER_ID);
        verify(dailyAttendanceRepository).save(domain);
    }

    @Test
    @DisplayName("correctDaily — lanza AttendanceNotFoundException si no existe")
    void correctDaily_whenNotFound_thenThrowsAttendanceNotFoundException() {
        // given
        CorrectAttendanceRequest request = new CorrectAttendanceRequest("PRESENT", null);

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(DAILY_ID)))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.correctDaily(DAILY_ID, request, USER_ID))
                .isInstanceOf(AttendanceNotFoundException.class)
                .hasMessageContaining(DAILY_ID.toString());

        verify(dailyAttendanceRepository, never()).save(any());
    }

    // ── course tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("correctCourse — cambia PRESENT a ABSENT y recalcula summary")
    void correctCourse_whenPresentToAbsent_thenRecalculatesSummary() {
        // given
        CourseAttendance domain = buildCourseDomain(AttendanceStatus.PRESENT);
        CorrectAttendanceRequest request = new CorrectAttendanceRequest("ABSENT", "Corrección");
        AttendanceSummary existingSummary = AttendanceSummary.create(
                AttendanceSummaryId.generate(),
                domain.getStudentCourseSubjectId(),
                domain.getCourseSubjectId(),
                domain.getPeriodId());

        when(courseAttendanceRepository.findById(CourseAttendanceId.of(COURSE_ID)))
                .thenReturn(Optional.of(domain));
        when(courseAttendanceRepository.save(domain)).thenReturn(domain);
        when(mapper.toCourseAttendanceResponse(any())).thenReturn(null);
        when(courseAttendanceRepository.findAllByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(List.of(domain));
        when(attendanceSummaryRepository.findByStudentCourseSubjectIdAndPeriodId(any(), any()))
                .thenReturn(Optional.of(existingSummary));
        when(attendanceSummaryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        useCase.correctCourse(COURSE_ID, request, USER_ID);

        // then
        assertThat(domain.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
        ArgumentCaptor<AttendanceSummary> captor = ArgumentCaptor.forClass(AttendanceSummary.class);
        verify(attendanceSummaryRepository).save(captor.capture());
        // Después de corregir a ABSENT: peso=1.0 sobre 1 clase → atRisk (>0.15)
        assertThat(captor.getValue().getWeightedAbsences()).isEqualTo(1.0);
        assertThat(captor.getValue().isAtRisk()).isTrue();
    }

    @Test
    @DisplayName("correctCourse — lanza AttendanceNotFoundException si no existe")
    void correctCourse_whenNotFound_thenThrowsAttendanceNotFoundException() {
        // given
        CorrectAttendanceRequest request = new CorrectAttendanceRequest("ABSENT", null);

        when(courseAttendanceRepository.findById(CourseAttendanceId.of(COURSE_ID)))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.correctCourse(COURSE_ID, request, USER_ID))
                .isInstanceOf(AttendanceNotFoundException.class)
                .hasMessageContaining(COURSE_ID.toString());

        verify(courseAttendanceRepository, never()).save(any());
        verify(attendanceSummaryRepository, never()).save(any());
    }
}