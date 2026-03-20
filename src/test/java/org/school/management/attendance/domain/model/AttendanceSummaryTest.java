package org.school.management.attendance.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests de la lógica de dominio de AttendanceSummary.recalculate().
 * No usa mocks — verifica la regla de negocio IPET 132 directamente.
 */
@Tag("unit")
@DisplayName("AttendanceSummary — recalculate()")
class AttendanceSummaryTest {

    private static final UUID SCS_ID    = UUID.randomUUID();
    private static final UUID CS_ID     = UUID.randomUUID();
    private static final UUID PERIOD_ID = UUID.randomUUID();
    private static final UUID USER_ID   = UUID.randomUUID();

    private AttendanceSummary summary;

    @BeforeEach
    void setUp() {
        summary = AttendanceSummary.create(
                AttendanceSummaryId.generate(),
                StudentCourseSubjectId.of(SCS_ID),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private CourseAttendance record(AttendanceStatus status) {
        return CourseAttendance.create(
                CourseAttendanceId.generate(),
                StudentCourseSubjectId.of(SCS_ID),
                CourseSubjectId.of(CS_ID),
                PeriodId.of(PERIOD_ID),
                LocalDate.now(), status, null, USER_ID);
    }

    private List<CourseAttendance> records(int present, int absent, int late, int withdrawn) {
        List<CourseAttendance> list = new java.util.ArrayList<>();
        IntStream.range(0, present).forEach(  i -> list.add(record(AttendanceStatus.PRESENT)));
        IntStream.range(0, absent).forEach(   i -> list.add(record(AttendanceStatus.ABSENT)));
        IntStream.range(0, late).forEach(     i -> list.add(record(AttendanceStatus.LATE)));
        IntStream.range(0, withdrawn).forEach(i -> list.add(record(AttendanceStatus.WITHDRAWN)));
        return list;
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("recalculate — lista vacía → 100% asistencia, no en riesgo")
    void recalculate_whenEmpty_thenFullAttendance() {
        summary.recalculate(Collections.emptyList());

        assertThat(summary.getTotalClasses()).isZero();
        assertThat(summary.getAttendancePercentage()).isEqualTo(100.0);
        assertThat(summary.isAtRisk()).isFalse();
        assertThat(summary.getWeightedAbsences()).isZero();
    }

    @Test
    @DisplayName("recalculate — 10 PRESENT → 100% y no en riesgo")
    void recalculate_whenAllPresent_thenFullAttendance() {
        summary.recalculate(records(10, 0, 0, 0));

        assertThat(summary.getTotalClasses()).isEqualTo(10);
        assertThat(summary.getPresentCount()).isEqualTo(10);
        assertThat(summary.getWeightedAbsences()).isEqualTo(0.0);
        assertThat(summary.getAttendancePercentage()).isEqualTo(100.0);
        assertThat(summary.isAtRisk()).isFalse();
    }

    @Test
    @DisplayName("recalculate — exactamente 15% de faltas → NO en riesgo (límite)")
    void recalculate_whenExactlyAtLimit_thenNotAtRisk() {
        // 20 clases, 3 ausentes → 3/20 = 0.15 exacto → NO libre (> 0.15 es libre)
        summary.recalculate(records(17, 3, 0, 0));

        assertThat(summary.getTotalClasses()).isEqualTo(20);
        assertThat(summary.getWeightedAbsences()).isEqualTo(3.0);
        assertThat(summary.getAttendancePercentage()).isEqualTo(85.0);
        assertThat(summary.isAtRisk()).isFalse();  // 3/20 = 0.15 NO es > 0.15
    }

    @Test
    @DisplayName("recalculate — más de 15% de faltas → en riesgo")
    void recalculate_whenAboveLimit_thenAtRisk() {
        // 20 clases, 4 ausentes → 4/20 = 0.20 > 0.15 → libre
        summary.recalculate(records(16, 4, 0, 0));

        assertThat(summary.isAtRisk()).isTrue();
        assertThat(summary.getAttendancePercentage()).isCloseTo(80.0, within(0.01));
    }

    @Test
    @DisplayName("recalculate — LATE tiene peso 0.2 (5 tardanzas = 1 falta)")
    void recalculate_whenLate_thenWeightIs02() {
        // 10 clases, 5 tardanzas → weightedAbsences = 5 * 0.2 = 1.0 → 10% < 15% → no libre
        summary.recalculate(records(5, 0, 5, 0));

        assertThat(summary.getLateCount()).isEqualTo(5);
        assertThat(summary.getWeightedAbsences()).isEqualTo(1.0);
        assertThat(summary.getAttendancePercentage()).isCloseTo(90.0, within(0.01));
        assertThat(summary.isAtRisk()).isFalse();
    }

    @Test
    @DisplayName("recalculate — JUSTIFIED tiene el mismo peso que ABSENT (1.0)")
    void recalculate_whenJustified_thenWeightSameAsAbsent() {
        // JUSTIFIED no exime la falta, solo registra el motivo
        List<CourseAttendance> withJustified = new java.util.ArrayList<>(records(17, 0, 0, 0));
        withJustified.add(record(AttendanceStatus.JUSTIFIED));
        withJustified.add(record(AttendanceStatus.JUSTIFIED));
        withJustified.add(record(AttendanceStatus.JUSTIFIED));
        // 20 clases, 3 justificadas → peso = 3.0 → 15% exacto → NO libre
        summary.recalculate(withJustified);

        assertThat(summary.getJustifiedCount()).isEqualTo(3);
        assertThat(summary.getWeightedAbsences()).isEqualTo(3.0); // mismo que ABSENT
        assertThat(summary.getAttendancePercentage()).isEqualTo(85.0);
        assertThat(summary.isAtRisk()).isFalse();
    }

    @Test
    @DisplayName("recalculate — WITHDRAWN tiene peso 0.2 (igual que LATE)")
    void recalculate_whenWithdrawn_thenWeightIs02() {
        summary.recalculate(records(9, 0, 0, 1));

        assertThat(summary.getWithdrawnCount()).isEqualTo(1);
        assertThat(summary.getWeightedAbsences()).isEqualTo(0.2);
        assertThat(summary.isAtRisk()).isFalse();
    }

    @Test
    @DisplayName("recalculate — mix de estados acumula pesos correctamente")
    void recalculate_whenMixedStatuses_thenAccumulatesWeightsCorrectly() {
        // 20 clases: 10 present + 2 absent + 1 justified + 5 late + 2 withdrawn
        // weighted = 2*1.0 + 1*1.0 + 5*0.2 + 2*0.2 = 2 + 1 + 1 + 0.4 = 4.4
        // → 4.4/20 = 0.22 > 0.15 → at risk
        List<CourseAttendance> mixed = new java.util.ArrayList<>(records(10, 2, 5, 2));
        mixed.add(record(AttendanceStatus.JUSTIFIED));
        summary.recalculate(mixed);

        assertThat(summary.getTotalClasses()).isEqualTo(20);
        assertThat(summary.getPresentCount()).isEqualTo(10);
        assertThat(summary.getAbsentCount()).isEqualTo(2);
        assertThat(summary.getJustifiedCount()).isEqualTo(1);
        assertThat(summary.getLateCount()).isEqualTo(5);
        assertThat(summary.getWithdrawnCount()).isEqualTo(2);
        assertThat(summary.getWeightedAbsences()).isCloseTo(4.4, within(0.001));
        assertThat(summary.isAtRisk()).isTrue();
    }

    @Test
    @DisplayName("recalculate — invocaciones sucesivas reflejan el estado actual")
    void recalculate_whenCalledTwice_thenReflectsLatestState() {
        // Primera carga: 5 presentes
        summary.recalculate(records(5, 0, 0, 0));
        assertThat(summary.getTotalClasses()).isEqualTo(5);
        assertThat(summary.isAtRisk()).isFalse();

        // Segunda carga: ahora 20 clases con 4 ausentes → at risk
        summary.recalculate(records(16, 4, 0, 0));
        assertThat(summary.getTotalClasses()).isEqualTo(20);
        assertThat(summary.getAbsentCount()).isEqualTo(4);
        assertThat(summary.isAtRisk()).isTrue();
    }
}