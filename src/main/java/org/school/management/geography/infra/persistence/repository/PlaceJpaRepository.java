package org.school.management.geography.infra.persistence.repository;

import org.school.management.geography.infra.persistence.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// JpaRepository
public interface PlaceJpaRepository extends JpaRepository<PlaceEntity, UUID> {
    List<PlaceEntity> findByProvinceId(UUID provinceId);

    Optional<PlaceEntity> findByNameAndProvinceId(String name, UUID provinceId);
}
