package org.school.management.grades.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationEntity {
    @Id
    @Column(name = "evaluation_id", columnDefinition = "BINARY(16)")
    private UUID evaluationId;

    @Column(name = "student_course_subject_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID studentCourseSubjectId;

    @Column(name = "period_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID periodId;

    @Column(name = "evaluation_type_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID evaluationTypeId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    @Column(name = "grade", precision = 4, scale = 2)
    private BigDecimal grade;

    @Column(name = "max_grade", nullable = false, precision = 4, scale = 2)
    private BigDecimal maxGrade;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated;

    @Column(name = "validated_by", columnDefinition = "BINARY(16)")
    private UUID validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "teacher_observations", columnDefinition = "TEXT")
    private String teacherObservations;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, columnDefinition = "BINARY(16)")
    private UUID createdBy;

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
