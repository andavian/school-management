package org.school.management.attendance.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.attendance.application.dto.request.RecordDailyAttendanceRequest;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.attendance.domain.exception.AttendanceAlreadyRecordedException;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RecordDailyAttendanceUseCase")
class RecordDailyAttendanceUseCaseTest {

    @Mock private DailyAttendanceRepository dailyAttendanceRepository;
    @Mock private AttendanceApplicationMapper mapper;
    @InjectMocks private RecordDailyAttendanceUseCase useCase;

    private static final UUID STUDENT_ID      = UUID.randomUUID();
    private static final UUID GRADE_LEVEL_ID  = UUID.randomUUID();
    private static final UUID ACADEMIC_YEAR_ID = UUID.randomUUID();
    private static final UUID RECORDED_BY     = UUID.randomUUID();
    private static final LocalDate TODAY      = LocalDate.now();

    // ── helpers ──────────────────────────────────────────────────────────────

    private RecordDailyAttendanceRequest buildRequest(String status) {
        return new RecordDailyAttendanceRequest(
                STUDENT_ID, GRADE_LEVEL_ID, ACADEMIC_YEAR_ID, TODAY, status, null);
    }

    private DailyAttendance buildDomain(AttendanceStatus status) {
        return DailyAttendance.create(
                DailyAttendanceId.generate(),
                StudentPersonalDataId.of(STUDENT_ID),
                GradeLevelId.of(GRADE_LEVEL_ID),
                AcademicYearId.of(ACADEMIC_YEAR_ID),
                TODAY, status, null, RECORDED_BY);
    }

    private DailyAttendanceResponse buildResponse(String status) {
        return new DailyAttendanceResponse(
                UUID.randomUUID(), STUDENT_ID, GRADE_LEVEL_ID, ACADEMIC_YEAR_ID,
                TODAY, status, null, null, RECORDED_BY,
                LocalDateTime.now(), LocalDateTime.now());
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — registra PRESENT y retorna respuesta")
    void execute_whenPresent_thenSavesAndReturnsResponse() {
        // given
        RecordDailyAttendanceRequest request = buildRequest("PRESENT");
        DailyAttendance domain  = buildDomain(AttendanceStatus.PRESENT);
        DailyAttendanceResponse expected = buildResponse("PRESENT");

        when(dailyAttendanceRepository.existsByStudentIdAndDate(
                StudentPersonalDataId.of(STUDENT_ID), TODAY)).thenReturn(false);
        when(dailyAttendanceRepository.save(any())).thenReturn(domain);
        when(mapper.toDailyAttendanceResponse(domain)).thenReturn(expected);

        // when
        DailyAttendanceResponse result = useCase.execute(request, RECORDED_BY);

        // then
        assertThat(result).isEqualTo(expected);
        verify(dailyAttendanceRepository).save(any(DailyAttendance.class));
    }

    @Test
    @DisplayName("execute — registra ABSENT correctamente")
    void execute_whenAbsent_thenSavesWithAbsentStatus() {
        // given
        RecordDailyAttendanceRequest request = buildRequest("ABSENT");
        DailyAttendance domain  = buildDomain(AttendanceStatus.ABSENT);
        DailyAttendanceResponse expected = buildResponse("ABSENT");

        when(dailyAttendanceRepository.existsByStudentIdAndDate(any(), any())).thenReturn(false);
        when(dailyAttendanceRepository.save(any())).thenReturn(domain);
        when(mapper.toDailyAttendanceResponse(domain)).thenReturn(expected);

        // when
        DailyAttendanceResponse result = useCase.execute(request, RECORDED_BY);

        // then
        assertThat(result.status()).isEqualTo("ABSENT");
    }

    @Test
    @DisplayName("execute — lanza AttendanceAlreadyRecordedException si ya existe registro para ese día")
    void execute_whenAlreadyRecorded_thenThrowsAlreadyRecordedException() {
        // given
        RecordDailyAttendanceRequest request = buildRequest("PRESENT");

        when(dailyAttendanceRepository.existsByStudentIdAndDate(
                StudentPersonalDataId.of(STUDENT_ID), TODAY)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> useCase.execute(request, RECORDED_BY))
                .isInstanceOf(AttendanceAlreadyRecordedException.class)
                .hasMessageContaining(STUDENT_ID.toString());

        verify(dailyAttendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — lanza IllegalArgumentException si status es inválido")
    void execute_whenInvalidStatus_thenThrowsIllegalArgumentException() {
        // given
        RecordDailyAttendanceRequest request = buildRequest("INVALID_STATUS");


        // when / then
        assertThatThrownBy(() -> useCase.execute(request, RECORDED_BY))
                .isInstanceOf(IllegalArgumentException.class);

        verify(dailyAttendanceRepository, never()).save(any());
    }
}