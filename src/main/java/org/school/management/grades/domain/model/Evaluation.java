package org.school.management.grades.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.academic.domain.valueobject.enums.EvaluationStatus;
import org.school.management.academic.domain.valueobject.ids.EvaluationId;
import org.school.management.academic.domain.valueobject.ids.EvaluationTypeId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evaluation {

    private static final BigDecimal MIN_PASSING_GRADE = BigDecimal.valueOf(7);

    @EqualsAndHashCode.Include
    private final EvaluationId evaluationId;
    private final StudentCourseSubjectId studentCourseSubjectId;
    private final PeriodId periodId;
    private final EvaluationTypeId evaluationTypeId;

    private final String title;
    private final String description;
    private final LocalDate evaluationDate;

    private final BigDecimal grade;
    private final BigDecimal maxGrade;

    private final EvaluationStatus status;
    private final boolean isValidated;
    private final UUID validatedBy;
    private final LocalDateTime validatedAt;

    private final String teacherObservations;
    private final String adminNotes;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID createdBy;

    public static Evaluation create(
            StudentCourseSubjectId studentCourseSubjectId,
            PeriodId periodId,
            EvaluationTypeId evaluationTypeId,
            String title,
            String description,
            LocalDate evaluationDate,
            UUID createdBy
    ) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Evaluation title cannot be blank");
        if (evaluationDate == null)
            throw new IllegalArgumentException("Evaluation date cannot be null");

        return Evaluation.builder()
                .evaluationId(EvaluationId.generate())
                .studentCourseSubjectId(studentCourseSubjectId)
                .periodId(periodId)
                .evaluationTypeId(evaluationTypeId)
                .title(title.trim())
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
        if (grade == null)
            throw new IllegalArgumentException("Grade cannot be null");
        if (grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(maxGrade) > 0)
            throw new IllegalArgumentException("Grade must be between 0 and " + maxGrade);
        if (status == EvaluationStatus.VALIDATED || status == EvaluationStatus.CANCELLED)
            throw new IllegalStateException("Cannot grade an evaluation in status: " + status);

        return this.toBuilder()
                .grade(grade)
                .status(EvaluationStatus.GRADED)
                .teacherObservations(observations)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Evaluation validate(UUID validatedBy) {
        if (grade == null)
            throw new IllegalStateException("Cannot validate evaluation without grade");
        if (isValidated)
            throw new IllegalStateException("Evaluation is already validated");

        return this.toBuilder()
                .isValidated(true)
                .validatedBy(validatedBy)
                .validatedAt(LocalDateTime.now())
                .status(EvaluationStatus.VALIDATED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Evaluation cancel() {
        if (isValidated)
            throw new IllegalStateException("Cannot cancel a validated evaluation");

        return this.toBuilder()
                .status(EvaluationStatus.CANCELLED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean isPassed() {
        return grade != null && grade.compareTo(MIN_PASSING_GRADE) >= 0;
    }

    public boolean isGraded() {
        return grade != null;
    }
}