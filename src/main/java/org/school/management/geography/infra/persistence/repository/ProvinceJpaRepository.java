package org.school.management.geography.infra.persistence.repository;

import org.school.management.geography.infra.persistence.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvinceJpaRepository extends JpaRepository<ProvinceEntity, UUID> {

    List<ProvinceEntity> findByCountryId(UUID countryId);

    Optional<ProvinceEntity> findByNameAndCountryId(String name, UUID countryId);

    Optional<ProvinceEntity> findByCode(String code);

    @Query("""
        SELECT p FROM ProvinceEntity p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))
        ORDER BY p.name
        """)
    List<ProvinceEntity> searchByName(@Param("namePattern") String namePattern);

    boolean existsByNameAndCountryId(String name, UUID countryId);

    boolean existsByCode(String code);

    long countByCountryId(UUID countryId);
}
