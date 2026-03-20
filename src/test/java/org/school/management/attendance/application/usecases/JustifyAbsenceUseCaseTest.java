package org.school.management.attendance.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.attendance.application.dto.request.JustifyAbsenceRequest;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("JustifyAbsenceUseCase")
class JustifyAbsenceUseCaseTest {

    @Mock private DailyAttendanceRepository dailyAttendanceRepository;
    @Mock private AttendanceApplicationMapper mapper;
    @InjectMocks private JustifyAbsenceUseCase useCase;

    private static final UUID ATTENDANCE_ID = UUID.randomUUID();
    private static final UUID USER_ID       = UUID.randomUUID();

    // ── helpers ──────────────────────────────────────────────────────────────

    private DailyAttendance buildDomainWithStatus(AttendanceStatus status) {
        return DailyAttendance.create(
                DailyAttendanceId.of(ATTENDANCE_ID),
                StudentPersonalDataId.of(UUID.randomUUID()),
                GradeLevelId.of(UUID.randomUUID()),
                AcademicYearId.of(UUID.randomUUID()),
                LocalDate.now(), status, null, UUID.randomUUID());
    }

    private DailyAttendanceResponse buildResponse(String status) {
        return new DailyAttendanceResponse(
                ATTENDANCE_ID, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), status, "Certificado médico", null, USER_ID,
                LocalDateTime.now(), LocalDateTime.now());
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — justifica una ausencia ABSENT → JUSTIFIED correctamente")
    void execute_whenAbsent_thenJustifiesAndSaves() {
        // given
        DailyAttendance absentRecord = buildDomainWithStatus(AttendanceStatus.ABSENT);
        DailyAttendanceResponse expected = buildResponse("JUSTIFIED");
        JustifyAbsenceRequest request = new JustifyAbsenceRequest("Certificado médico");

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(ATTENDANCE_ID)))
                .thenReturn(Optional.of(absentRecord));
        when(dailyAttendanceRepository.save(absentRecord)).thenReturn(absentRecord);
        when(mapper.toDailyAttendanceResponse(absentRecord)).thenReturn(expected);

        // when
        DailyAttendanceResponse result = useCase.execute(ATTENDANCE_ID, request, USER_ID);

        // then
        assertThat(result.status()).isEqualTo("JUSTIFIED");
        assertThat(absentRecord.getStatus()).isEqualTo(AttendanceStatus.JUSTIFIED);
        assertThat(absentRecord.getJustificationReason()).isEqualTo("Certificado médico");
        verify(dailyAttendanceRepository).save(absentRecord);
    }

    @Test
    @DisplayName("execute — lanza AttendanceNotFoundException si el registro no existe")
    void execute_whenNotFound_thenThrowsAttendanceNotFoundException() {
        // given
        JustifyAbsenceRequest request = new JustifyAbsenceRequest("Motivo");

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(ATTENDANCE_ID)))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(ATTENDANCE_ID, request, USER_ID))
                .isInstanceOf(AttendanceNotFoundException.class)
                .hasMessageContaining(ATTENDANCE_ID.toString());

        verify(dailyAttendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — lanza IllegalStateException si el estado ya es PRESENT")
    void execute_whenPresent_thenThrowsIllegalStateException() {
        // given
        DailyAttendance presentRecord = buildDomainWithStatus(AttendanceStatus.PRESENT);
        JustifyAbsenceRequest request = new JustifyAbsenceRequest("Motivo");

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(ATTENDANCE_ID)))
                .thenReturn(Optional.of(presentRecord));

        // when / then
        assertThatThrownBy(() -> useCase.execute(ATTENDANCE_ID, request, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PRESENT");

        verify(dailyAttendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — lanza IllegalStateException si el estado ya es JUSTIFIED")
    void execute_whenAlreadyJustified_thenThrowsIllegalStateException() {
        // given
        DailyAttendance justifiedRecord = buildDomainWithStatus(AttendanceStatus.JUSTIFIED);
        JustifyAbsenceRequest request = new JustifyAbsenceRequest("Otro motivo");

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(ATTENDANCE_ID)))
                .thenReturn(Optional.of(justifiedRecord));

        // when / then
        assertThatThrownBy(() -> useCase.execute(ATTENDANCE_ID, request, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JUSTIFIED");

        verify(dailyAttendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — lanza IllegalStateException si el estado es LATE")
    void execute_whenLate_thenThrowsIllegalStateException() {
        // given
        DailyAttendance lateRecord = buildDomainWithStatus(AttendanceStatus.LATE);
        JustifyAbsenceRequest request = new JustifyAbsenceRequest("Motivo");

        when(dailyAttendanceRepository.findById(DailyAttendanceId.of(ATTENDANCE_ID)))
                .thenReturn(Optional.of(lateRecord));

        // when / then
        assertThatThrownBy(() -> useCase.execute(ATTENDANCE_ID, request, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("LATE");

        verify(dailyAttendanceRepository, never()).save(any());
    }
}