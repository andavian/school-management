package org.school.management.course.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_course_subjects")
public class StudentCourseSubjectEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_course_subject_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID studentCourseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "enrollment_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID enrollmentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "course_subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID courseSubjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubjectEnrollmentStatus status;

    @Column(name = "total_classes")
    private int totalClasses;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}