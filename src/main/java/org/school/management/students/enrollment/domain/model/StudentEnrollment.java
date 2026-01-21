package org.school.management.students.enrollment.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.enrollment.domain.exception.*;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentType;
import org.school.management.academic.domain.valueobject.ids.WithdrawalReasonId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Agregado Root: Inscripción del Estudiante
 *
 * Responsabilidad: Gestionar la matrícula del estudiante en un ciclo lectivo específico
 * Un estudiante puede tener múltiples enrollments (uno por año, o varios si repite)
 *
 * Reglas de negocio críticas:
 * - Solo puede haber UNA inscripción ACTIVE por estudiante por año académico
 * - Una vez COMPLETED o WITHDRAWN, no se puede reactivar (estados terminales)
 * - El promedio final solo se registra al completar el año
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class StudentEnrollment {

    // Identidad
    @EqualsAndHashCode.Include
    private final EnrollmentId enrollmentId;
    private final StudentPersonalDataId studentId;
    private final AcademicYearId academicYearId;
    private final GradeLevelId gradeLevelId;

    // Datos de inscripción
    private final LocalDate enrollmentDate;
    private final EnrollmentType enrollmentType;
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    // Información de origen
    private final boolean isRepeating;
    private final String previousSchool;
    private final LocalDate transferDate;

    // Cierre del ciclo lectivo
    private BigDecimal finalAverage;
    private Boolean passed;
    private LocalDate completionDate;

    // Baja (withdrawal)
    private LocalDate withdrawalDate;
    private WithdrawalReasonId withdrawalReasonId;
    private String withdrawalObservations;

    // Auditoría
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ============ Domain Logic ============

    /**
     * Completa el año académico con promedio final
     */
    public void complete(BigDecimal finalAverage, boolean passed) {
        validateCanComplete();
        validateFinalAverage(finalAverage);

        this.finalAverage = finalAverage;
        this.passed = passed;
        this.completionDate = LocalDate.now();
        this.status = EnrollmentStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Se gradua de la secundaria
     */
    public void graduate() {
        if (status != EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot graduate a student who hasn't completed their studies.");
        }

        this.status = EnrollmentStatus.GRADUATED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Registra la baja del estudiante
     */
    public void withdraw(WithdrawalReasonId reasonId, String observations) {
        Objects.requireNonNull(reasonId, "Withdrawal reason cannot be null");

        if (status == EnrollmentStatus.WITHDRAWN) {
            throw new EnrollmentAlreadyWithdrawnException(
                    "Enrollment is already withdrawn: " + enrollmentId
            );
        }

        if (status == EnrollmentStatus.COMPLETED) {
            throw new EnrollmentAlreadyCompletedException(
                    "Cannot withdraw a completed enrollment: " + enrollmentId
            );
        }

        this.withdrawalDate = LocalDate.now();
        this.withdrawalReasonId = reasonId;
        this.withdrawalObservations = observations;
        this.status = EnrollmentStatus.WITHDRAWN;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateCanComplete() {
        if (status == EnrollmentStatus.COMPLETED) {
            throw new EnrollmentAlreadyCompletedException(
                    "Enrollment is already completed: " + enrollmentId
            );
        }

        if (status == EnrollmentStatus.WITHDRAWN) {
            throw new EnrollmentAlreadyWithdrawnException(
                    "Cannot complete a withdrawn enrollment: " + enrollmentId
            );
        }
    }

    private void validateFinalAverage(BigDecimal average) {
        if (average == null) {
            throw new InvalidEnrollmentCompletionException(
                    "Final average cannot be null"
            );
        }

        if (average.compareTo(BigDecimal.ONE) < 0 ||
                average.compareTo(BigDecimal.TEN) > 0) {
            throw new InvalidEnrollmentCompletionException(
                    "Final average must be between 1 and 10, got: " + average
            );
        }
    }

    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
    }

    public boolean canReceiveGrades() {
        return status.canReceiveGrades();
    }

    public boolean isTransfer() {
        return enrollmentType == EnrollmentType.TRANSFER;
    }

    public Boolean hasPassed() {
        return passed;
    }

    public long getDurationInDays() {
        LocalDate endDate = switch (status) {
            case ACTIVE, INACTIVE, SUSPENDED, TRANSFERRED -> LocalDate.now();
            case COMPLETED, GRADUATED -> completionDate != null ? completionDate : LocalDate.now();
            case WITHDRAWN -> withdrawalDate != null ? withdrawalDate : LocalDate.now();

        };

        return java.time.temporal.ChronoUnit.DAYS.between(enrollmentDate, endDate);
    }

    // Factory method para encapsular lógica de creación
    public static StudentEnrollment create(StudentEnrollmentBuilder builder) {
        Objects.requireNonNull(builder.enrollmentId, "EnrollmentId cannot be null");
        Objects.requireNonNull(builder.studentId, "StudentId cannot be null");
        Objects.requireNonNull(builder.academicYearId, "AcademicYearId cannot be null");
        Objects.requireNonNull(builder.gradeLevelId, "GradeLevelId cannot be null");
        Objects.requireNonNull(builder.enrollmentDate, "Enrollment date cannot be null");
        Objects.requireNonNull(builder.enrollmentType, "Enrollment type cannot be null");

        if (builder.enrollmentType == EnrollmentType.TRANSFER) {
            if (builder.previousSchool == null || builder.previousSchool.isBlank()) {
                throw new InvalidEnrollmentException(
                        "Previous school is required for TRANSFER enrollment"
                );
            }
        }

        if (builder.enrollmentDate.isAfter(LocalDate.now())) {
            throw new InvalidEnrollmentException(
                    "Enrollment date cannot be in the future"
            );
        }

        return builder.build();
    }
}