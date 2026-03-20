// org.school.management.attendance.domain.model.DailyAttendance
package org.school.management.attendance.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de asistencia diaria tomado por el preceptor (STAFF).
 * El preceptor puede justificar una ausencia posterior a su registro.
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DailyAttendance {

    @EqualsAndHashCode.Include
    private final DailyAttendanceId dailyAttendanceId;

    private final StudentPersonalDataId studentId;
    private final GradeLevelId gradeLevelId;
    private final AcademicYearId academicYearId;
    private final LocalDate attendanceDate;

    private AttendanceStatus status;
    private String justificationReason;
    private String observations;

    private final UUID recordedByUserId;
    private UUID correctedByUserId;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DailyAttendance create(DailyAttendanceId id,
                                         StudentPersonalDataId studentId,
                                         GradeLevelId gradeLevelId,
                                         AcademicYearId academicYearId,
                                         LocalDate attendanceDate,
                                         AttendanceStatus status,
                                         String observations,
                                         UUID recordedByUserId) {
        if (id == null)             throw new IllegalArgumentException("DailyAttendanceId is required");
        if (studentId == null)      throw new IllegalArgumentException("StudentId is required");
        if (gradeLevelId == null)   throw new IllegalArgumentException("GradeLevelId is required");
        if (academicYearId == null) throw new IllegalArgumentException("AcademicYearId is required");
        if (attendanceDate == null) throw new IllegalArgumentException("AttendanceDate is required");
        if (status == null)         throw new IllegalArgumentException("AttendanceStatus is required");
        if (recordedByUserId == null) throw new IllegalArgumentException("RecordedByUserId is required");

        LocalDateTime now = LocalDateTime.now();
        return DailyAttendance.builder()
                .dailyAttendanceId(id)
                .studentId(studentId)
                .gradeLevelId(gradeLevelId)
                .academicYearId(academicYearId)
                .attendanceDate(attendanceDate)
                .status(status)
                .observations(observations)
                .recordedByUserId(recordedByUserId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * El preceptor justifica una ausencia. ABSENT → JUSTIFIED.
     * No cambia el peso de falta — solo registra el motivo.
     */
    public void justify(String reason, UUID justifiedByUserId) {
        if (!status.canBeJustified()) {
            throw new IllegalStateException(
                    "Cannot justify attendance with status: " + status + ". Only ABSENT can be justified.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Justification reason is required");
        }
        this.status = AttendanceStatus.JUSTIFIED;
        this.justificationReason = reason;
        this.correctedByUserId = justifiedByUserId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Corrección del registro por el preceptor.
     */
    public void correct(AttendanceStatus newStatus, String newObservations, UUID correctedByUserId) {
        if (newStatus == null) throw new IllegalArgumentException("New status is required");
        this.status = newStatus;
        this.observations = newObservations;
        this.correctedByUserId = correctedByUserId;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAbsent() {
        return status.isAbsent();
    }
}