package org.school.management.grades.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.valueobject.PeriodGradeId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class PeriodGrade {
    PeriodGradeId periodGradeId;
    StudentCourseSubjectId studentCourseSubjectId;
    PeriodId periodId;

    @With
    BigDecimal averageGrade;      // Calculado automáticamente
    BigDecimal adjustedGrade;            // Ajuste manual del profesor
    @With
    BigDecimal finalPeriodGrade;  // Nota final del período

    Boolean isPassed;
    @With
    boolean isValidated;
    UUID validatedBy;
    LocalDateTime validatedAt;

    String observations;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

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
        if (evaluationGrades == null || evaluationGrades.isEmpty()) {
            return this;
        }

        BigDecimal sum = evaluationGrades.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = sum.divide(
                BigDecimal.valueOf(evaluationGrades.size()),
                2,
                RoundingMode.HALF_UP
        );

        return this.withAverageGrade(average)
                .withFinalPeriodGrade(average);
    }

    public PeriodGrade adjustGrade(BigDecimal adjustedGrade, String observations) {
        return this.toBuilder()
                .adjustedGrade(adjustedGrade)
                .finalPeriodGrade(adjustedGrade)
                .observations(observations)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PeriodGrade validate(UUID validatedBy) {
        if (finalPeriodGrade == null) {
            throw new IllegalStateException("Cannot validate without final grade");
        }

        boolean passed = finalPeriodGrade.compareTo(BigDecimal.valueOf(6)) >= 0;

        return this.toBuilder()
                .isPassed(passed)
                .isValidated(true)
                .validatedBy(validatedBy)
                .validatedAt(LocalDateTime.now())
                .build();
    }
}
