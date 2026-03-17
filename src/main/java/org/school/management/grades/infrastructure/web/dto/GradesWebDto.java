package org.school.management.grades.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.school.management.grades.domain.valueobject.EvaluationStatus;
import org.school.management.grades.domain.valueobject.FinalGradeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class GradesWebDto {

    private GradesWebDto() {}

    // =========================================================
    // REQUESTS
    // =========================================================

    public record CreateEvaluationWebRequest(

            @NotNull(message = "studentCourseSubjectId is required")
            UUID studentCourseSubjectId,

            @NotNull(message = "periodId is required")
            UUID periodId,

            @NotNull(message = "evaluationTypeId is required")
            UUID evaluationTypeId,

            @NotBlank(message = "title is required")
            @Size(max = 200, message = "title must not exceed 200 characters")
            String title,

            @Size(max = 1000, message = "description must not exceed 1000 characters")
            String description,

            @NotNull(message = "evaluationDate is required")
            LocalDate evaluationDate
    ) {}

    public record GradeEvaluationWebRequest(

            @NotNull(message = "grade is required")
            @DecimalMin(value = "0.00", message = "grade must be at least 0")
            @DecimalMax(value = "10.00", message = "grade must not exceed 10")
            BigDecimal grade,

            @Size(max = 1000, message = "observations must not exceed 1000 characters")
            String teacherObservations
    ) {}

    public record RecordExamGradeWebRequest(

            @NotNull(message = "studentCourseSubjectId is required")
            UUID studentCourseSubjectId,

            @NotNull(message = "academicYearId is required")
            UUID academicYearId,

            @NotNull(message = "examGrade is required")
            @DecimalMin(value = "0.00", message = "examGrade must be at least 0")
            @DecimalMax(value = "10.00", message = "examGrade must not exceed 10")
            BigDecimal examGrade,

            @Size(max = 1000, message = "observations must not exceed 1000 characters")
            String observations
    ) {}

    public record RecordFinalGradeInRegistryWebRequest(

            @NotNull(message = "studentId is required")
            UUID studentId
    ) {}

    // =========================================================
    // RESPONSES
    // =========================================================

    public record EvaluationWebResponse(
            UUID evaluationId,
            UUID studentCourseSubjectId,
            UUID periodId,
            UUID evaluationTypeId,
            String title,
            String description,
            LocalDate evaluationDate,
            BigDecimal grade,
            BigDecimal maxGrade,
            EvaluationStatus status,
            boolean isValidated,
            UUID validatedBy,
            LocalDateTime validatedAt,
            String teacherObservations,
            String adminNotes,
            boolean isPassed,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record PeriodGradeWebResponse(
            UUID periodGradeId,
            UUID studentCourseSubjectId,
            UUID periodId,
            BigDecimal averageGrade,
            BigDecimal adjustedGrade,
            BigDecimal finalPeriodGrade,
            Boolean isPassed,
            boolean isValidated,
            UUID validatedBy,
            LocalDateTime validatedAt,
            String observations,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record FinalGradeWebResponse(
            UUID finalGradeId,
            UUID studentCourseSubjectId,
            UUID academicYearId,
            BigDecimal periodAverage,
            BigDecimal finalExamGrade,
            BigDecimal finalGrade,
            FinalGradeStatus status,
            boolean isValidated,
            UUID validatedBy,
            LocalDateTime validatedAt,
            boolean recordedInRegistry,
            UUID registryId,
            Integer folioNumber,
            LocalDateTime recordedAt,
            String observations,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}