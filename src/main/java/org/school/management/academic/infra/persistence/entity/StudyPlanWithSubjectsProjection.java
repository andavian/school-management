package org.school.management.academic.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// ============================================================================
// STUDY PLAN WITH SUBJECTS PROJECTION
// Para queries que necesitan Study Plan + lista de Subjects
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanWithSubjectsProjection {
    // StudyPlan fields
    private UUID studyPlanId;
    private String planName;
    private String planCode;
    private Integer yearLevel;
    private String description;
    private Integer totalWeeklyHours;

    // Orientation fields (nullable)
    private UUID orientationId;
    private String orientationName;
    private String orientationCode;

    // Aggregated data
    private Integer totalSubjects;
    private Integer coreSubjects;
    private Integer orientationSubjects;

    public boolean isCommonPlan() {
        return orientationId == null;
    }

    public String getFullPlanName() {
        if (isCommonPlan()) {
            return planName + " - Ciclo Común";
        }
        return planName + " - " + orientationName;
    }
}
