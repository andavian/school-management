package org.school.management.grades.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.enums.EvaluationStatus;
import org.school.management.academic.domain.valueobject.ids.EvaluationId;
import org.school.management.academic.domain.valueobject.ids.EvaluationTypeId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Evaluation {
    EvaluationId evaluationId;
    StudentCourseSubjectId studentCourseSubjectId;
    PeriodId periodId;
    EvaluationTypeId evaluationTypeId;

    String title;
    String description;
    LocalDate evaluationDate;

    @With
    BigDecimal grade;
    BigDecimal maxGrade;

    @With
    EvaluationStatus status;
    @With
    boolean isValidated;
    UUID validatedBy;
    LocalDateTime validatedAt;

    String teacherObservations;
    String adminNotes;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UUID createdBy;

    public static Evaluation create(
            StudentCourseSubjectId studentCourseSubjectId,
            PeriodId periodId,
            EvaluationTypeId evaluationTypeId,
            String title,
            String description,
            LocalDate evaluationDate,
            UUID createdBy
    ) {
        return Evaluation.builder()
                .evaluationId(EvaluationId.generate())
                .studentCourseSubjectId(studentCourseSubjectId)
                .periodId(periodId)
                .evaluationTypeId(evaluationTypeId)
                .title(title)
                .description(description)
                .evaluationDate(evaluationDate)
                .maxGrade(BigDecimal.TEN)
                .status(EvaluationStatus.PENDING)
                .isValidated(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    public Evaluation gradeEvaluation(BigDecimal grade, String observations) {
        if (grade.compareTo(BigDecimal.ZERO) < 0 ||
                grade.compareTo(maxGrade) > 0) {
            throw new IllegalArgumentException("Grade must be between 0 and " + maxGrade);
        }

        return this.toBuilder()
                .grade(grade)
                .status(EvaluationStatus.GRADED)
                .teacherObservations(observations)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Evaluation validate(UUID validatedBy) {
        if (grade == null) {
            throw new IllegalStateException("Cannot validate evaluation without grade");
        }

        return this.toBuilder()
                .isValidated(true)
                .validatedBy(validatedBy)
                .validatedAt(LocalDateTime.now())
                .status(EvaluationStatus.VALIDATED)
                .build();
    }

    public boolean isPassed() {
        return grade != null && grade.compareTo(BigDecimal.valueOf(6)) >= 0;
    }
}
