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
@Table(name = "final_grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalGradeEntity {
    @Id
    @Column(name = "final_grade_id", columnDefinition = "BINARY(16)")
    private UUID finalGradeId;

    @Column(name = "student_course_subject_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID studentCourseSubjectId;

    @Column(name = "academic_year_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID academicYearId;

    @Column(name = "period_average", precision = 4, scale = 2)
    private BigDecimal periodAverage;

    @Column(name = "final_exam_grade", precision = 4, scale = 2)
    private BigDecimal finalExamGrade;

    @Column(name = "final_grade", nullable = false, precision = 4, scale = 2)
    private BigDecimal finalGrade;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated;

    @Column(name = "validated_by", columnDefinition = "BINARY(16)")
    private UUID validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "recorded_in_registry", nullable = false)
    private Boolean recordedInRegistry;

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
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
