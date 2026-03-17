package org.school.management.grades.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.grades.domain.valueobject.FinalGradeStatus;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "final_grades")
public class FinalGradeEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "final_grade_id", columnDefinition = "BINARY(16)",
            updatable = false, nullable = false)
    private UUID finalGradeId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_course_subject_id",
            columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentCourseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID academicYearId;

    @Column(name = "period_average", precision = 4, scale = 2)
    private BigDecimal periodAverage;

    @Column(name = "final_exam_grade", precision = 4, scale = 2)
    private BigDecimal finalExamGrade;

    @Column(name = "final_grade", nullable = false, precision = 4, scale = 2)
    private BigDecimal finalGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FinalGradeStatus status;

    @Column(name = "is_validated", nullable = false)
    private boolean isValidated;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "validated_by", columnDefinition = "BINARY(16)")
    private UUID validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "recorded_in_registry", nullable = false)
    private boolean recordedInRegistry;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "registry_id", columnDefinition = "BINARY(16)")
    private UUID registryId;

    @Column(name = "folio_number")
    private Integer folioNumber;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

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