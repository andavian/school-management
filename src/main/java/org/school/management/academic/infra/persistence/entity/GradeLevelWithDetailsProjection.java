package org.school.management.academic.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;

import java.util.UUID;

// ============================================================================
// GRADE LEVEL WITH DETAILS PROJECTION
// Incluye: GradeLevel + AcademicYear + Orientation
// ============================================================================

@Data
@NoArgsConstructor
public class GradeLevelWithDetailsProjection {
    // GradeLevel fields
    private UUID gradeLevelId;
    private Integer yearLevel;
    private String division;
    private String shift;
    private Integer maxStudents;
    private UUID homeroomTeacherId;

    // AcademicYear fields
    private UUID academicYearId;
    private Integer year;
    private AcademicYearStatus status;

    // Orientation fields (nullable)
    private UUID orientationId;
    private String orientationName;
    private String orientationCode;


    // 🔥 CONSTRUCTOR EXACTO para la query
    public GradeLevelWithDetailsProjection(
            UUID gradeLevelId,
            Integer yearLevel,
            String division,
            String shift,
            Integer maxStudents,
            UUID homeroomTeacherId,
            UUID academicYearId,
            Integer year,
            AcademicYearStatus status,
            UUID orientationId,
            String orientationName,
            String orientationCode
    ) {
        this.gradeLevelId = gradeLevelId;
        this.yearLevel = yearLevel;
        this.division = division;
        this.shift = shift;
        this.maxStudents = maxStudents;
        this.homeroomTeacherId = homeroomTeacherId;
        this.academicYearId = academicYearId;
        this.year = year;
        this.status = status;
        this.orientationId = orientationId;
        this.orientationName = orientationName;
        this.orientationCode = orientationCode;
        // Los campos de teacher se quedan null - no están en el SELECT
    }



    public String getDisplayName() {
             return yearLevel + "° " + division;
    }

    public String getFullDisplayName() {
        String base = getDisplayName();
        if (orientationName != null) {
            base += " - " + orientationName;
        }
        return base;
    }

    public boolean hasOrientation() {
        return orientationId != null;
    }

}
