package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.*;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.valueobject.WeeklyHours;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.academic.infra.persistence.entity.SubjectEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SubjectMapper {

    @Mapping(target = "subjectId", source = "subjectId")
    @Mapping(target = "yearLevel", source = "yearLevel")
    @Mapping(target = "orientationId", source = "orientationId")
    @Mapping(target = "code", source = "code.value")
    SubjectEntity toEntity(Subject domain);

    @InheritInverseConfiguration
    Subject toDomain(SubjectEntity entity);


    default UUID mapSubjectId(SubjectId id) {
        return id != null ? id.getValue() : null;
    }

    default SubjectId mapSubjectId(UUID uuid) {
        return uuid != null ? new SubjectId(uuid) : null;
    }

    default Integer mapYearLevel(YearLevel yearLevel) {
        return yearLevel != null ? yearLevel.getValue() : null;
    }

    default YearLevel mapYearLevel(Integer value) {
        return value != null ? YearLevel.of(value) : null;
    }

    default Integer mapWeeklyHours(WeeklyHours value) {
        return value != null ? value.getValue() : null;
    }

    default WeeklyHours mapWeeklyHours(Integer value) {
        return value != null ? WeeklyHours.of(value) : null;
    }



    default UUID mapOrientationId(OrientationId id) {
        return id != null ? id.getValue() : null;
    }

    default OrientationId mapOrientationId(UUID uuid) {
        return uuid != null ? new OrientationId(uuid) : null;
    }
}