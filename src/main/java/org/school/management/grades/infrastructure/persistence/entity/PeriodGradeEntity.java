package org.school.management.grades.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "period_grades")
public class PeriodGradeEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "period_grade_id", columnDefinition = "BINARY(16)",
            updatable = false, nullable = false)
    private UUID periodGradeId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_course_subject_id",
            columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentCourseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "period_id", columnDefinition = "BINARY(16)", nullable = false)
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
    private boolean isValidated;

    @Convert(converter = UuidBinaryConverter.class)
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