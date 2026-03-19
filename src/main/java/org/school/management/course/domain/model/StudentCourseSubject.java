package org.school.management.course.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudentCourseSubject {

    @EqualsAndHashCode.Include
    private final StudentCourseSubjectId studentCourseSubjectId;

    private final UUID enrollmentId;
    private final UUID courseSubjectId;

    private SubjectEnrollmentStatus status;

    // Solo total_classes está en BD — asistencia individual no se persiste aquí
    private int totalClasses;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Factory method
    // -------------------------------------------------------------------------

    public static StudentCourseSubject enroll(
            UUID enrollmentId,
            UUID courseSubjectId
    ) {
        LocalDateTime now = LocalDateTime.now();
        return StudentCourseSubject.builder()
                .studentCourseSubjectId(StudentCourseSubjectId.generate())
                .enrollmentId(enrollmentId)
                .courseSubjectId(courseSubjectId)
                .status(SubjectEnrollmentStatus.ENROLLED)
                .totalClasses(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    public void startAttending() {
        if (this.status != SubjectEnrollmentStatus.ENROLLED) {
            throw new IllegalStateException(
                    "Cannot start attending from status: " + this.status);
        }
        this.status = SubjectEnrollmentStatus.ATTENDING;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordClass() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot record class for a subject in status: " + this.status);
        }
        this.totalClasses++;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFree() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot mark as free a subject already in status: " + this.status);
        }
        this.status = SubjectEnrollmentStatus.FREE;
        this.updatedAt = LocalDateTime.now();
    }

    public void pass() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot pass a subject already in status: " + this.status);
        }
        this.status = SubjectEnrollmentStatus.PASSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot fail a subject already in status: " + this.status);
        }
        this.status = SubjectEnrollmentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void pendingExam() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot set pending exam for a subject in status: " + this.status);
        }
        this.status = SubjectEnrollmentStatus.PENDING_EXAM;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot withdraw from a subject already in status: " + this.status);
        }
        this.status = SubjectEnrollmentStatus.WITHDRAWN;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status.isActive();
    }
}