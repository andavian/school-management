package org.school.management.course.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_course_subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCourseSubjectEntity {
    @Id
    @Column(name = "student_course_subject_id", columnDefinition = "BINARY(16)")
    private UUID studentCourseSubjectId;

    @Column(name = "enrollment_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID enrollmentId;

    @Column(name = "course_subject_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID courseSubjectId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "total_classes")
    private Integer totalClasses;

    @Column(name = "attended_classes")
    private Integer attendedClasses;

    @Column(name = "attendance_pct", precision = 5, scale = 2)
    private BigDecimal attendancePercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
