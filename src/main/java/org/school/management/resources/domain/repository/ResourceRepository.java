package org.school.management.resources.domain.repository;

import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para persistencia de Resources (catálogo/familias).
 * Implementado por ResourceRepositoryAdapter en infrastructure/persistence/adapter/
 */
public interface ResourceRepository {

    Optional<Resource> findByResourceId(ResourceId id);

    Optional<Resource> findByCode(String code);

    boolean existsByCode(String code);

    List<Resource> findByResourceType(ResourceType type);

    List<Resource> findAllActive();

    List<Resource> findAllActiveAndReservable();

    Resource save(Resource resource);

    void delete(ResourceId id); // Soft delete vía resource.deactivate() + persist
}