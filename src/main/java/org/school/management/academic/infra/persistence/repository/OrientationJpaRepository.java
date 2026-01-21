package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.OrientationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrientationJpaRepository extends JpaRepository<OrientationEntity, UUID> {
    Optional<OrientationEntity> findByCode(String code);

    Optional<OrientationEntity> findByName(String name);

    List<OrientationEntity> findByIsActiveTrue();

    List<OrientationEntity> findByAvailableFromYear(Integer yearLevel);

    @Query("SELECT o FROM OrientationEntity o WHERE o.availableFromYear <= :yearLevel AND o.isActive = true")
    List<OrientationEntity> findAvailableForYearLevel(@Param("yearLevel") Integer yearLevel);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    long countByIsActiveTrue();

    @Query("SELECT o FROM OrientationEntity o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(o.code) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<OrientationEntity> search(@Param("search") String search);
}
