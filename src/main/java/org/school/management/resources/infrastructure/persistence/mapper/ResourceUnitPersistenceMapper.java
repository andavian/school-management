package org.school.management.resources.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;
import org.school.management.resources.infrastructure.persistence.entity.ResourceUnitEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourceUnitPersistenceMapper {

    default ResourceUnitEntity toEntity(ResourceUnit domain) {
        ResourceUnitEntity entity = new ResourceUnitEntity();
        entity.setUnitId(domain.getUnitId().value());
        entity.setResourceId(domain.getResourceId().value());
        entity.setUnitCode(domain.getUnitCode());
        entity.setSerialNumber(domain.getSerialNumber());
        entity.setConditionStatus(domain.getConditionStatus());
        entity.setUnitStatus(domain.getUnitStatus());
        entity.setNotes(domain.getNotes());
        return entity;
    }

    default ResourceUnit toDomain(ResourceUnitEntity entity) {
        return ResourceUnit.builder()
                .unitId(UnitId.of(entity.getUnitId()))
                .resourceId(ResourceId.of(entity.getResourceId()))
                .unitCode(entity.getUnitCode())
                .serialNumber(entity.getSerialNumber())
                .conditionStatus(entity.getConditionStatus() != null ? entity.getConditionStatus() : ConditionStatus.GOOD)
                .unitStatus(entity.getUnitStatus() != null ? entity.getUnitStatus() : UnitStatus.AVAILABLE)
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}