// src/main/java/org/school/management/resources/infrastructure/persistence/mapper/ResourcePersistenceMapper.java
package org.school.management.resources.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.infrastructure.persistence.entity.ResourceEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourcePersistenceMapper {

    default ResourceEntity toEntity(Resource domain) {
        if (domain == null) return null;

        ResourceEntity entity = new ResourceEntity();
        entity.setResourceId(domain.getResourceId().value());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setResourceType(domain.getResourceType());
        entity.setDescription(domain.getDescription());
        entity.setLocation(domain.getLocation());
        entity.setReservable(domain.isReservable());
        entity.setNotes(domain.getNotes());
        entity.setActive(domain.isActive());
        entity.setCreatedBy(domain.getCreatedBy());           // ← Agregado
        // createdAt y updatedAt se manejan con @PrePersist / @PreUpdate
        return entity;
    }

    default Resource toDomain(ResourceEntity entity) {
        if (entity == null) return null;

        return Resource.builder()
                .resourceId(ResourceId.of(entity.getResourceId()))
                .name(entity.getName())
                .code(entity.getCode())
                .resourceType(entity.getResourceType())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .reservable(entity.isReservable())
                .notes(entity.getNotes())
                .active(entity.isActive())
                .createdBy(entity.getCreatedBy())               // ← Agregado
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}