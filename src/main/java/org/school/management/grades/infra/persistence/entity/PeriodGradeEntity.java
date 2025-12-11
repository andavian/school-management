package org.school.management.grades.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "period_grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodGradeEntity {
    @Id
    @Column(name = "period_grade_id", columnDefinition = "BINARY(16)")
    private UUID periodGradeId;

    @Column(name = "student_course_subject_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID studentCourseSubjectId;

    @Column(name = "period_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID periodId;

    @Column(name = "average_grade", precision = 4, scale = 2)
    private BigDecimal averageGrade;

    @Column(name = "adjusted_grade", precision = 4, scale = 2)
    private BigDecimal adjustedGrade;

    @Column(name = "final_period_grade", precision = 4, scale = 2)
    private BigDecimal finalPeriodGrade;

    @Column(name = "is_passed")
    private Boolean isPassed;

    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated;

    @Column(name = "validated_by", columnDefinition = "BINARY(16)")
    private UUID validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

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
