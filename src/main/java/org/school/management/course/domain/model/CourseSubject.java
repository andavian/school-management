package org.school.management.course.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.CourseStatus;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CourseSubject {

    @EqualsAndHashCode.Include
    private final CourseSubjectId courseSubjectId;

    private final GradeLevelId gradeLevelId;
    private final SubjectId subjectId;
    private TeacherId teacherId;          // nullable — puede asignarse después
    private final AcademicYearId academicYearId;

    private String scheduleJson;
    private String classroom;

    private BigDecimal minPassingGrade;

    private CourseStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Factory method
    // -------------------------------------------------------------------------

    public static CourseSubject create(
            GradeLevelId gradeLevelId,
            SubjectId subjectId,
            AcademicYearId academicYearId,
            TeacherId teacherId           // nullable
    ) {
        LocalDateTime now = LocalDateTime.now();
        return CourseSubject.builder()
                .courseSubjectId(CourseSubjectId.generate())
                .gradeLevelId(gradeLevelId)
                .subjectId(subjectId)
                .teacherId(teacherId)
                .academicYearId(academicYearId)
                .minPassingGrade(BigDecimal.valueOf(6.00))
                .status(CourseStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    public void assignTeacher(TeacherId teacherId) {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot assign teacher to a course in status: " + this.status);
        }
        this.teacherId = teacherId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSchedule(String scheduleJson, String classroom) {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot update schedule of a course in status: " + this.status);
        }
        this.scheduleJson = scheduleJson;
        this.classroom = classroom;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status == CourseStatus.COMPLETED) {
            throw new IllegalStateException("CourseSubject is already completed");
        }
        this.status = CourseStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot deactivate a course in status: " + this.status);
        }
        this.status = CourseStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasTeacher() {
        return teacherId != null;
    }

    public boolean isActive() {
        return this.status == CourseStatus.ACTIVE;
    }
}