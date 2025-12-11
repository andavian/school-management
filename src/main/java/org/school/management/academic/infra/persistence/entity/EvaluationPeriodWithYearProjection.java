package org.school.management.academic.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// ============================================================================
// EVALUATION PERIOD WITH YEAR PROJECTION
// Incluye: EvaluationPeriod + AcademicYear
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationPeriodWithYearProjection {
    // EvaluationPeriod fields
    private UUID evaluationPeriodId;
    private Integer periodNumber;
    private String periodName;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private java.time.LocalDate gradeSubmissionDeadline;
    private Boolean isActive;

    // AcademicYear fields
    private UUID academicYearId;
    private Integer year;
    private Boolean isCurrentYear;

    public String getDisplayName() {
        return periodName + " " + year;
    }

    public boolean isGradeSubmissionOpen() {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        return !today.isAfter(gradeSubmissionDeadline);
    }

    public boolean isCurrentPeriod() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return Boolean.TRUE.equals(isActive)
                && !today.isBefore(startDate)
                && !today.isAfter(endDate);
    }
}
