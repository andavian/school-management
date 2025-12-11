package org.school.management.academic.domain.model;

import org.school.management.academic.domain.valueobject.*;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.StudyPlanId;

import java.time.LocalDateTime;


@Value
@Builder(toBuilder = true)
public class StudyPlan {
    StudyPlanId studyPlanId;
    String name;
    String code;
    String description;
    OrientationId orientationId;  // Null para ciclo básico
    YearLevel yearLevel;
    @With Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static StudyPlan create(
            String name,
            String code,
            String description,
            int yearLevel,
            OrientationId orientationId
    ) {
        return StudyPlan.builder()
                .studyPlanId(StudyPlanId.generate())
                .name(name)
                .code(code)
                .description(description)
                .yearLevel(YearLevel.of(yearLevel))
                .orientationId(orientationId)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean isGenerlPlan() {
        return orientationId == null;
    }

    public boolean isForBasicCycle(){
        return yearLevel.isBasicCycle();
    }
    public boolean isOrientedPlan() {
        return orientationId != null;
    }

    public boolean isForOrientedCycle() {
        return yearLevel.isOrientedCycle();
    }
}

