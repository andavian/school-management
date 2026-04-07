package org.school.management.resources.infrastructure.persistence.repository;

import org.school.management.resources.domain.valueobject.UnitStatus;
import org.school.management.resources.infrastructure.persistence.entity.ResourceUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceUnitJpaRepository extends JpaRepository<ResourceUnitEntity, UUID> {

    Optional<ResourceUnitEntity> findByUnitCode(String unitCode);

    boolean existsByUnitCode(String unitCode);

    List<ResourceUnitEntity> findByResourceIdAndUnitStatus(UUID resourceId, UnitStatus status);

    List<ResourceUnitEntity> findByResourceId(UUID resourceId);
}