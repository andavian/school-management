package org.school.management.academic.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// ============================================================================
// SUBJECT WITH ORIENTATION PROJECTION
// Incluye: Subject + Orientation (si aplica)
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectWithOrientationProjection {
    // Subject fields
    private UUID subjectId;
    private String subjectName;
    private String subjectCode;
    private Integer yearLevel;
    private Integer weeklyHours;
    private Boolean isActive;

    // Orientation fields (nullable)
    private UUID orientationId;
    private String orientationName;
    private String orientationCode;

    public boolean isCommonSubject() {
        return orientationId == null;
    }

    public boolean isOrientationSpecific() {
        return orientationId != null;
    }

    public String getDisplayName() {
        if (isCommonSubject()) {
            return subjectName + " (Común)";
        }
        return subjectName + " (" + orientationName + ")";
    }
}
