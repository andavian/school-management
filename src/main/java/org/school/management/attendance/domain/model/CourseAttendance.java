// org.school.management.attendance.domain.model.CourseAttendance
package org.school.management.attendance.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de asistencia por materia tomado por el docente (TEACHER).
 * Cada clase dictada genera uno de estos registros por alumno.
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CourseAttendance {

    @EqualsAndHashCode.Include
    private final CourseAttendanceId courseAttendanceId;

    private final StudentCourseSubjectId studentCourseSubjectId;
    private final CourseSubjectId courseSubjectId;
    private final PeriodId periodId;
    private final LocalDate classDate;

    private AttendanceStatus status;
    private String observations;

    private final UUID recordedByUserId;
    private UUID correctedByUserId;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseAttendance create(CourseAttendanceId id,
                                          StudentCourseSubjectId studentCourseSubjectId,
                                          CourseSubjectId courseSubjectId,
                                          PeriodId periodId,
                                          LocalDate classDate,
                                          AttendanceStatus status,
                                          String observations,
                                          UUID recordedByUserId) {
        if (id == null)                     throw new IllegalArgumentException("CourseAttendanceId is required");
        if (studentCourseSubjectId == null) throw new IllegalArgumentException("StudentCourseSubjectId is required");
        if (courseSubjectId == null)        throw new IllegalArgumentException("CourseSubjectId is required");
        if (periodId == null)               throw new IllegalArgumentException("PeriodId is required");
        if (classDate == null)              throw new IllegalArgumentException("ClassDate is required");
        if (status == null)                 throw new IllegalArgumentException("AttendanceStatus is required");
        if (recordedByUserId == null)       throw new IllegalArgumentException("RecordedByUserId is required");

        LocalDateTime now = LocalDateTime.now();
        return CourseAttendance.builder()
                .courseAttendanceId(id)
                .studentCourseSubjectId(studentCourseSubjectId)
                .courseSubjectId(courseSubjectId)
                .periodId(periodId)
                .classDate(classDate)
                .status(status)
                .observations(observations)
                .recordedByUserId(recordedByUserId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Corrección del registro por el docente.
     */
    public void correct(AttendanceStatus newStatus, String newObservations, UUID correctedByUserId) {
        if (newStatus == null) throw new IllegalArgumentException("New status is required");
        this.status = newStatus;
        this.observations = newObservations;
        this.correctedByUserId = correctedByUserId;
        this.updatedAt = LocalDateTime.now();
    }

    public double getAbsenceWeight() {
        return status.getAbsenceWeight();
    }
}