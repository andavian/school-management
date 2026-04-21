package org.school.management.students.enrollment.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "student_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_student_year_grade",
                        columnNames = {"student_id", "academic_year_id"}
                )
        }
)
public class StudentEnrollmentEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "enrollment_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID enrollmentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID academicYearId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "grade_level_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID gradeLevelId;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_type", nullable = false, length = 20)
    private EnrollmentType enrollmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "is_repeating", nullable = false)
    private boolean isRepeating;

    @Column(name = "previous_school", length = 200)
    private String previousSchool;

    @Column(name = "transfer_date")
    private LocalDate transferDate;

    // Cierre de ciclo
    @Column(name = "final_average", precision = 3, scale = 2)
    private BigDecimal finalAverage;

    @Column(name = "passed", nullable = true)
    private Boolean passed;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    // Baja
    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "withdrawal_reason_id", columnDefinition = "BINARY(16)")
    private UUID withdrawalReasonId;

    @Column(name = "withdrawal_observations", columnDefinition = "TEXT")
    private String withdrawalObservations;

    // Auditoría
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