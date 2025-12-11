package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.*;
import org.school.management.academic.domain.model.StudyPlan;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.StudyPlanId;
import org.school.management.academic.infra.persistence.entity.StudyPlanEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudyPlanMapper {

    @Mapping(target = "studyPlanId", source = "studyPlanId")
    @Mapping(target = "yearLevel", source = "yearLevel")
    @Mapping(target = "orientationId", source = "orientationId")
    StudyPlanEntity toEntity(StudyPlan domain);

    @InheritInverseConfiguration
    StudyPlan toDomain(StudyPlanEntity entity);

    default UUID mapStudyPlanId(StudyPlanId id) {
        return id != null ? id.getValue() : null;
    }

    default StudyPlanId mapStudyPlanId(UUID uuid) {
        return uuid != null ? new StudyPlanId(uuid) : null;
    }

    default Integer mapYearLevel(YearLevel yearLevel) {
        return yearLevel != null ? yearLevel.getValue() : null;
    }

    default YearLevel mapYearLevel(Integer value) {
        return value != null ? YearLevel.of(value) : null;
    }

    default UUID mapOrientationId(OrientationId id) {
        return id != null ? id.getValue() : null;
    }

    default OrientationId mapOrientationId(UUID uuid) {
        return uuid != null ? new OrientationId(uuid) : null;
    }
}
