package org.school.management.grades.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.valueobject.FinalGradeId;
import org.school.management.grades.domain.valueobject.FinalGradeStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class FinalGrade {
    FinalGradeId finalGradeId;
    StudentCourseSubjectId studentCourseSubjectId;
    AcademicYearId academicYearId;

    BigDecimal periodAverage;
    BigDecimal finalExamGrade;
    @With
    BigDecimal finalGrade;

    @With
    FinalGradeStatus status;

    @With
    boolean isValidated;
    UUID validatedBy;
    LocalDateTime validatedAt;

    @With
    boolean recordedInRegistry;
    RegistryId registryId;
    Integer folioNumber;
    LocalDateTime recordedAt;

    String observations;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static FinalGrade create(
            StudentCourseSubjectId studentCourseSubjectId,
            AcademicYearId academicYearId,
            List<BigDecimal> periodGrades
    ) {
        if (periodGrades == null || periodGrades.isEmpty()) {
            throw new IllegalArgumentException("Period grades are required");
        }

        BigDecimal average = periodGrades.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(periodGrades.size()), 2, RoundingMode.HALF_UP);

        FinalGradeStatus status = average.compareTo(BigDecimal.valueOf(6)) >= 0
                ? FinalGradeStatus.PASSED
                : FinalGradeStatus.PENDING_EXAM;

        return FinalGrade.builder()
                .finalGradeId(FinalGradeId.generate())
                .studentCourseSubjectId(studentCourseSubjectId)
                .academicYearId(academicYearId)
                .periodAverage(average)
                .finalGrade(average)
                .status(status)
                .isValidated(false)
                .recordedInRegistry(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public FinalGrade recordExam(BigDecimal examGrade) {
        // Nota final = (Promedio períodos + Examen) / 2
        BigDecimal newFinalGrade = periodAverage
                .add(examGrade)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        FinalGradeStatus newStatus = newFinalGrade.compareTo(BigDecimal.valueOf(6)) >= 0
                ? FinalGradeStatus.PASSED
                : FinalGradeStatus.FAILED;

        return this.toBuilder()
                .finalExamGrade(examGrade)
                .finalGrade(newFinalGrade)
                .status(newStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public FinalGrade validate(UUID validatedBy) {
        return this.toBuilder()
                .isValidated(true)
                .validatedBy(validatedBy)
                .validatedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public FinalGrade recordInRegistry(RegistryId registryId, int folioNumber) {
        if (!isValidated) {
            throw new IllegalStateException("Cannot record unvalidated grade in registry");
        }

        return this.toBuilder()
                .recordedInRegistry(true)
                .registryId(registryId)
                .folioNumber(folioNumber)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    public boolean requiresExam() {
        return status == FinalGradeStatus.PENDING_EXAM;
    }

    public boolean isPassed() {
        return status == FinalGradeStatus.PASSED;
    }
}
