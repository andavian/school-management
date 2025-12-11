package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.*;
import org.school.management.academic.domain.valueobject.enums.Shift;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.OrientationId;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class GradeLevel {
    GradeLevelId gradeLevelId;
    AcademicYearId academicYearId;
    YearLevel yearLevel;
    Division division;
    OrientationId orientationId;  // Nullable para 1°-3°
    Shift shift;
    int maxStudents;
    UUID homeroomTeacherId;  // Nullable
    @With
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static GradeLevel create(
            AcademicYearId academicYearId,
            int yearLevel,
            String division,
            OrientationId orientationId,
            Shift shift,
            int maxStudents
    ) {
        YearLevel level = YearLevel.of(yearLevel);

        // Validar orientación
        if (level.requiresOrientation() && orientationId == null) {
            throw new IllegalArgumentException(
                    "Orientation is required for year level " + yearLevel
            );
        }

        if (!level.requiresOrientation() && orientationId != null) {
            throw new IllegalArgumentException(
                    "Orientation is not allowed for year level " + yearLevel
            );
        }

        if (maxStudents <= 0 || maxStudents > 50) {
            throw new IllegalArgumentException("Max students must be between 1 and 50");
        }

        return GradeLevel.builder()
                .gradeLevelId(GradeLevelId.generate())
                .academicYearId(academicYearId)
                .yearLevel(level)
                .division(Division.of(division))
                .orientationId(orientationId)
                .shift(shift)
                .maxStudents(maxStudents)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public GradeLevel assignHomeroomTeacher(UUID teacherId) {
        return this.toBuilder()
                .homeroomTeacherId(teacherId)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public String getDisplayName() {
        return yearLevel.getDisplayName() + " " + division.getValue();
    }

    public String getFullDisplayName(String orientationName) {
        String base = getDisplayName();
        if (orientationId != null && orientationName != null) {
            base += " - " + orientationName;
        }
        return base;
    }

    public int getYearLevelValue() {
        return yearLevel.getValue();
    }

    public String getDivisionValue() {
        return division.getValue();
    }

    public boolean hasOrientation() {
        return orientationId != null;
    }
}
