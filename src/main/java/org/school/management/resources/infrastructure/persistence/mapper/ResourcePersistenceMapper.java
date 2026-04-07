package org.school.management.resources.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.infrastructure.persistence.entity.ResourceEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourcePersistenceMapper {

    default ResourceEntity toEntity(Resource domain) {
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
        // created_at/updated_at se manejan vía @PrePersist/@PreUpdate en la entidad
        return entity;
    }

    default Resource toDomain(ResourceEntity entity) {
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
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}