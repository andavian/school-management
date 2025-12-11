package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.*;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Subject {
    SubjectId subjectId;
    String name;
    SubjectCode code;
    YearLevel yearLevel;
    OrientationId orientationId;  // Nullable = común a todas las orientaciones
    Boolean isMandatory;
    WeeklyHours weeklyHours;
    String description;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static Subject create(
            String name,
            String code,
            Integer yearLevel,
            OrientationId orientationId,
            Boolean isMandatory,
            Integer weeklyHours,
            String description
    ) {
        return Subject.builder()
                .subjectId(SubjectId.generate())
                .name(name)
                .code(SubjectCode.of(code))
                .yearLevel(YearLevel.of(yearLevel))
                .orientationId(orientationId)
                .isMandatory(isMandatory)
                .weeklyHours(WeeklyHours.of(weeklyHours))
                .description(description)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean isCommonSubject() {
        return orientationId == null;
    }

    public boolean isOrientationSpecific() {
        return orientationId != null;
    }

    public String getCodeAsString() {
        return code.getValue();
    }

    public int getYearLevelValue() {
        return yearLevel.getValue();
    }

    public int getWeeklyHoursValue() {
        return weeklyHours.getValue();
    }


}
