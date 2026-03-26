// org.school.management.attendance.domain.model.AttendanceSummary
package org.school.management.attendance.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resumen de asistencia de un alumno para una materia en un período.
 * Se recalcula automáticamente en cada carga o corrección de CourseAttendance.
 * Libre si weightedAbsences / totalClasses > 0.15 (MIN_ATTENDANCE_PERCENTAGE = 85%)
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttendanceSummary {

    public static final double MIN_ATTENDANCE_PERCENTAGE = 85.0;
    private static final double MAX_ABSENCE_RATIO = 0.15;

    @EqualsAndHashCode.Include
    private final AttendanceSummaryId attendanceSummaryId;

    private final StudentCourseSubjectId studentCourseSubjectId;
    private final CourseSubjectId courseSubjectId;
    private final PeriodId periodId;

    // Calculados por recalculate()
    private int totalClasses;
    private int presentCount;
    private int absentCount;
    private int justifiedCount;
    private int lateCount;
    private int withdrawnCount;
    private double weightedAbsences;      // suma ponderada de faltas
    private double attendancePercentage;  // (1 - weightedAbsences/totalClasses) * 100
    private boolean atRisk;               // weightedAbsences / totalClasses > 0.15

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AttendanceSummary create(AttendanceSummaryId id,
                                           StudentCourseSubjectId studentCourseSubjectId,
                                           CourseSubjectId courseSubjectId,
                                           PeriodId periodId) {
        if (id == null)                     throw new IllegalArgumentException("AttendanceSummaryId is required");
        if (studentCourseSubjectId == null) throw new IllegalArgumentException("StudentCourseSubjectId is required");
        if (courseSubjectId == null)        throw new IllegalArgumentException("CourseSubjectId is required");
        if (periodId == null)               throw new IllegalArgumentException("PeriodId is required");

        LocalDateTime now = LocalDateTime.now();
        return AttendanceSummary.builder()
                .attendanceSummaryId(id)
                .studentCourseSubjectId(studentCourseSubjectId)
                .courseSubjectId(courseSubjectId)
                .periodId(periodId)
                .totalClasses(0)
                .presentCount(0)
                .absentCount(0)
                .justifiedCount(0)
                .lateCount(0)
                .withdrawnCount(0)
                .weightedAbsences(0.0)
                .attendancePercentage(100.0)
                .atRisk(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Recalcula todos los contadores y el porcentaje de asistencia.
     * Debe llamarse después de cada carga o corrección de un CourseAttendance.
     */
    public void recalculate(List<CourseAttendance> allRecordsForPeriod) {
        if (allRecordsForPeriod == null || allRecordsForPeriod.isEmpty()) {
            resetCounters();
            this.updatedAt = LocalDateTime.now();
            return;
        }

        int total = allRecordsForPeriod.size();
        int present = 0, absent = 0, justified = 0, late = 0, withdrawn = 0;
        double weighted = 0.0;

        for (CourseAttendance record : allRecordsForPeriod) {
            weighted += record.getAbsenceWeight();
            switch (record.getStatus()) {
                case PRESENT   -> present++;
                case ABSENT    -> absent++;
                case JUSTIFIED -> justified++;
                case LATE      -> late++;
                case WITHDRAWN -> withdrawn++;
            }
        }

        this.totalClasses         = total;
        this.presentCount         = present;
        this.absentCount          = absent;
        this.justifiedCount       = justified;
        this.lateCount            = late;
        this.withdrawnCount       = withdrawn;
        this.weightedAbsences     = weighted;
        this.attendancePercentage = total > 0
                ? (1.0 - weighted / total) * 100.0
                : 100.0;
        this.atRisk = total >= 20 && (weighted / total) > MAX_ABSENCE_RATIO;
        this.updatedAt            = LocalDateTime.now();
    }

    private void resetCounters() {
        this.totalClasses         = 0;
        this.presentCount         = 0;
        this.absentCount          = 0;
        this.justifiedCount       = 0;
        this.lateCount            = 0;
        this.withdrawnCount       = 0;
        this.weightedAbsences     = 0.0;
        this.attendancePercentage = 100.0;
        this.atRisk               = false;
    }

    public boolean isAtRisk() {
        return atRisk;
    }
}