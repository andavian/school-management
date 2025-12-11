package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.*;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.infra.persistence.entity.OrientationEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrientationMapper {

    @Mapping(target = "orientationId", source = "orientationId")
    @Mapping(target = "code", source = "code.value")
    OrientationEntity toEntity(Orientation domain);

    @InheritInverseConfiguration
    Orientation toDomain(OrientationEntity entity);

    default UUID mapOrientationId(OrientationId id) {
        return id != null ? id.getValue() : null;
    }


    default Integer mapAvailableFromYear(YearLevel yearLevel) {
        return yearLevel != null ? yearLevel.getValue() : null;
    }

    default YearLevel mapAvailableFromYear(Integer value) {
        return value != null ? YearLevel.of(value) : null;
    }

    default OrientationId mapOrientationId(UUID uuid) {
        return uuid != null ? new OrientationId(uuid) : null;
    }
}
