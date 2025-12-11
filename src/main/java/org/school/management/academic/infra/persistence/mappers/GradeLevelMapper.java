package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.valueobject.Division;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.enums.Shift;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.infra.persistence.entity.GradeLevelEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GradeLevelMapper {

    @Mapping(target = "gradeLevelId", source = "gradeLevelId")
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "orientationId", source = "orientationId")
    @Mapping(target = "shift", source = "shift")
    @Mapping(target = "yearLevel", source = "yearLevel")
    @Mapping(target = "division", source = "division")
    GradeLevelEntity toEntity(GradeLevel domain);

    @InheritInverseConfiguration
    GradeLevel toDomain(GradeLevelEntity entity);


    default UUID mapGradeLevelId(GradeLevelId id) {
        return id != null ? id.getValue() : null;
    }

    default GradeLevelId mapGradeLevelId(UUID uuid) {
        return uuid != null ? new GradeLevelId(uuid) : null;
    }

    default UUID mapAcademicYearId(AcademicYearId id) {
        return id != null ? id.getValue() : null;
    }

    default AcademicYearId mapAcademicYearId(UUID uuid) {
        return uuid != null ? new AcademicYearId(uuid) : null;
    }

    default Integer mapYearLevel(YearLevel yearLevel) {
        return yearLevel != null ? yearLevel.getValue() : null;
    }

    default YearLevel mapYearLevel(Integer value) {
        return value != null ? YearLevel.of(value) : null;
    }

    default String mapDivision(Division division) {
        return division != null ? division.getValue() : null;
    }

    default Division mapDivision(String value) {
        return value != null ? Division.of(value) : null;
    }

    default UUID mapOrientationId(OrientationId id) {
        return id != null ? id.getValue() : null;
    }

    default OrientationId mapOrientationId(UUID uuid) {
        return uuid != null ? new OrientationId(uuid) : null;
    }

    default String mapShift(Shift shift) {
        return shift != null ? shift.name() : null;
    }

    default Shift mapShift(String shift) {
        return shift != null ? Shift.valueOf(shift) : null;
    }
}