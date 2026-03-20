// org.school.management.attendance.infrastructure.persistence.entity.CourseAttendanceEntity
package org.school.management.attendance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attendance_course_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_course_student_date",
                columnNames = {"student_course_subject_id", "class_date"}))
public class CourseAttendanceEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "course_attendance_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID courseAttendanceId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_course_subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentCourseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "course_subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID courseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "period_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID periodId;

    @Column(name = "class_date", nullable = false)
    private LocalDate classDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "observations", length = 500)
    private String observations;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "recorded_by_user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID recordedByUserId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "corrected_by_user_id", columnDefinition = "BINARY(16)")
    private UUID correctedByUserId;

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