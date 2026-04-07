package org.school.management.resources.infrastructure.persistence.repository;

import org.school.management.resources.domain.valueobject.ResourceType;
import org.school.management.resources.infrastructure.persistence.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, UUID> {

    Optional<ResourceEntity> findByCode(String code);

    boolean existsByCode(String code);

    List<ResourceEntity> findByResourceType(ResourceType resourceType);

    List<ResourceEntity> findByIsActiveTrue();

    List<ResourceEntity> findByIsActiveTrueAndIsReservableTrue();
}