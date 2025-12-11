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
@Table(name = "course_subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSubjectEntity {
    @Id
    @Column(name = "course_subject_id", columnDefinition = "BINARY(16)")
    private UUID courseSubjectId;

    @Column(name = "grade_level_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID gradeLevelId;

    @Column(name = "subject_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID subjectId;

    @Column(name = "teacher_id", columnDefinition = "BINARY(16)")
    private UUID teacherId;

    @Column(name = "academic_year_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID academicYearId;

    @Column(name = "schedule_json", columnDefinition = "JSON")
    private String scheduleJson;

    @Column(name = "classroom", length = 50)
    private String classroom;

    @Column(name = "min_passing_grade", nullable = false, precision = 4, scale = 2)
    private BigDecimal minPassingGrade;

    @Column(name = "requires_attendance", nullable = false)
    private Boolean requiresAttendance;

    @Column(name = "min_attendance_pct")
    private Integer minAttendancePercentage;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

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
