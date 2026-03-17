package org.school.management.grades.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.grades.domain.valueobject.EvaluationStatus;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "evaluations")
public class EvaluationEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "evaluation_id", columnDefinition = "BINARY(16)",
            updatable = false, nullable = false)
    private UUID evaluationId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_course_subject_id",
            columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentCourseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "period_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID periodId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "evaluation_type_id", columnDefinition = "BINARY(16)", nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EvaluationStatus status;

    @Column(name = "is_validated", nullable = false)
    private boolean isValidated;

    @Convert(converter = UuidBinaryConverter.class)
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

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID createdBy;

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