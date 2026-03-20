// org.school.management.attendance.infrastructure.persistence.entity.AttendanceSummaryEntity
package org.school.management.attendance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attendance_period_summaries",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_summary_student_period",
                columnNames = {"student_course_subject_id", "period_id"}))
public class AttendanceSummaryEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "attendance_summary_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID attendanceSummaryId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_course_subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentCourseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "course_subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID courseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "period_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID periodId;

    @Column(name = "total_classes", nullable = false)
    private int totalClasses;

    @Column(name = "present_count", nullable = false)
    private int presentCount;

    @Column(name = "absent_count", nullable = false)
    private int absentCount;

    @Column(name = "justified_count", nullable = false)
    private int justifiedCount;

    @Column(name = "late_count", nullable = false)
    private int lateCount;

    @Column(name = "withdrawn_count", nullable = false)
    private int withdrawnCount;

    @Column(name = "weighted_absences", nullable = false)
    private double weightedAbsences;

    @Column(name = "attendance_percentage", nullable = false)
    private double attendancePercentage;

    @Column(name = "at_risk", nullable = false)
    private boolean atRisk;

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