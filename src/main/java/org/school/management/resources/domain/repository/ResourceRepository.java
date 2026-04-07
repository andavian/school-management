package org.school.management.resources.domain.repository;

import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.ResourceStatus;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    Resource save(Resource resource);
    Optional<Resource> findById(ResourceId id);
    List<Resource> findAll();
    List<Resource> findByType(ResourceType type);
    List<Resource> findByStatus(ResourceStatus status);
    boolean existsById(ResourceId id);
    void delete(ResourceId id);
}