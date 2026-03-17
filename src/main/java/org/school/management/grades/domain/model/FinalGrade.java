package org.school.management.grades.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FinalGrade {

    private static final BigDecimal MIN_PASSING_GRADE = BigDecimal.valueOf(7);

    @EqualsAndHashCode.Include
    private final FinalGradeId finalGradeId;
    private final StudentCourseSubjectId studentCourseSubjectId;
    private final AcademicYearId academicYearId;

    private final BigDecimal periodAverage;
    private final BigDecimal finalExamGrade;
    private final BigDecimal finalGrade;

    private final FinalGradeStatus status;

    private final boolean isValidated;
    private final UUID validatedBy;
    private final LocalDateTime validatedAt;

    private final boolean recordedInRegistry;
    private final RegistryId registryId;
    private final Integer folioNumber;
    private final LocalDateTime recordedAt;

    private final String observations;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static FinalGrade create(
            StudentCourseSubjectId studentCourseSubjectId,
            AcademicYearId academicYearId,
            List<BigDecimal> periodGrades
    ) {
        if (periodGrades == null || periodGrades.isEmpty())
            throw new IllegalArgumentException("Period grades are required to calculate final grade");

        BigDecimal average = periodGrades.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(periodGrades.size()), 2, RoundingMode.HALF_UP);

        FinalGradeStatus status = average.compareTo(MIN_PASSING_GRADE) >= 0
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
        if (examGrade == null)
            throw new IllegalArgumentException("Exam grade cannot be null");
        if (examGrade.compareTo(BigDecimal.ZERO) < 0 || examGrade.compareTo(BigDecimal.TEN) > 0)
            throw new IllegalArgumentException("Exam grade must be between 0 and 10");
        if (status != FinalGradeStatus.PENDING_EXAM)
            throw new IllegalStateException("Student is not in PENDING_EXAM status");

        BigDecimal newFinalGrade = periodAverage
                .add(examGrade)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        FinalGradeStatus newStatus = newFinalGrade.compareTo(MIN_PASSING_GRADE) >= 0
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
        if (isValidated)
            throw new IllegalStateException("Final grade is already validated");

        return this.toBuilder()
                .isValidated(true)
                .validatedBy(validatedBy)
                .validatedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public FinalGrade recordInRegistry(RegistryId registryId, int folioNumber) {
        if (!isValidated)
            throw new IllegalStateException("Cannot record unvalidated grade in registry");
        if (recordedInRegistry)
            throw new IllegalStateException("Final grade is already recorded in registry");

        return this.toBuilder()
                .recordedInRegistry(true)
                .registryId(registryId)
                .folioNumber(folioNumber)
                .recordedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean requiresExam() {
        return status == FinalGradeStatus.PENDING_EXAM;
    }

    public boolean isPassed() {
        return status == FinalGradeStatus.PASSED;
    }
}
