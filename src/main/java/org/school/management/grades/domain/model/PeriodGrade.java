package org.school.management.grades.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.valueobject.PeriodGradeId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PeriodGrade {

    private static final BigDecimal MIN_PASSING_GRADE = BigDecimal.valueOf(7);

    @EqualsAndHashCode.Include
    private final PeriodGradeId periodGradeId;
    private final StudentCourseSubjectId studentCourseSubjectId;
    private final PeriodId periodId;

    private final BigDecimal averageGrade;
    private final BigDecimal adjustedGrade;
    private final BigDecimal finalPeriodGrade;

    private final Boolean isPassed;
    private final boolean isValidated;
    private final UUID validatedBy;
    private final LocalDateTime validatedAt;

    private final String observations;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PeriodGrade create(
            StudentCourseSubjectId studentCourseSubjectId,
            PeriodId periodId
    ) {
        return PeriodGrade.builder()
                .periodGradeId(PeriodGradeId.generate())
                .studentCourseSubjectId(studentCourseSubjectId)
                .periodId(periodId)
                .isValidated(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PeriodGrade calculateAverage(List<BigDecimal> evaluationGrades) {
        if (evaluationGrades == null || evaluationGrades.isEmpty())
            return this;

        BigDecimal average = evaluationGrades.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evaluationGrades.size()), 2, RoundingMode.HALF_UP);

        return this.toBuilder()
                .averageGrade(average)
                .finalPeriodGrade(average)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PeriodGrade adjustGrade(BigDecimal adjustedGrade, String observations) {
        if (adjustedGrade == null)
            throw new IllegalArgumentException("Adjusted grade cannot be null");
        if (adjustedGrade.compareTo(BigDecimal.ZERO) < 0 || adjustedGrade.compareTo(BigDecimal.TEN) > 0)
            throw new IllegalArgumentException("Adjusted grade must be between 0 and 10");
        if (isValidated)
            throw new IllegalStateException("Cannot adjust a validated period grade");

        return this.toBuilder()
                .adjustedGrade(adjustedGrade)
                .finalPeriodGrade(adjustedGrade)
                .observations(observations)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PeriodGrade validate(UUID validatedBy) {
        if (finalPeriodGrade == null)
            throw new IllegalStateException("Cannot validate period grade without final grade");
        if (isValidated)
            throw new IllegalStateException("Period grade is already validated");

        return this.toBuilder()
                .isPassed(finalPeriodGrade.compareTo(MIN_PASSING_GRADE) >= 0)
                .isValidated(true)
                .validatedBy(validatedBy)
                .validatedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean hasGrade() {
        return finalPeriodGrade != null;
    }
}