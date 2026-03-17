package org.school.management.grades.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordExamGradeRequest(

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