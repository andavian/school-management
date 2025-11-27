package org.school.management.geography.infra.persistence.repository;

import org.school.management.geography.infra.persistence.entity.PlaceEntity;
import org.school.management.geography.infra.persistence.entity.PlaceWithHierarchyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaceJpaRepository extends JpaRepository<PlaceEntity, UUID> {

    List<PlaceEntity> findByProvinceId(UUID provinceId);

    Optional<PlaceEntity> findByNameAndProvinceId(String name, UUID provinceId);

    List<PlaceEntity> findByType(PlaceEntity.PlaceTypeEnum type);

    List<PlaceEntity> findByProvinceIdAndType(UUID provinceId, PlaceEntity.PlaceTypeEnum type);

    List<PlaceEntity> findByPostalCode(String postalCode);

    @Query("""
        SELECT p FROM PlaceEntity p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))
        ORDER BY p.name
        """)
    List<PlaceEntity> searchByName(@Param("namePattern") String namePattern);

    @Query("""
        SELECT p FROM PlaceEntity p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))
        AND p.provinceId = :provinceId
        ORDER BY p.name
        """)
    List<PlaceEntity> searchByNameInProvince(
            @Param("namePattern") String namePattern,
            @Param("provinceId") UUID provinceId
    );

    boolean existsByNameAndProvinceId(String name, UUID provinceId);

    long countByProvinceId(UUID provinceId);

    long countByType(PlaceEntity.PlaceTypeEnum type);

    // Query con JOIN para obtener jerarquía completa
    @Query("SELECT new org.school.management.geography.infra.persistence.entity.PlaceWithHierarchyProjection(" +
            "p.placeId, p.name as placeName, p.type, p.postalCode, " +
            "pr.provinceId, pr.name as provinceName, pr.code, " +
            "c.countryId, c.name as countryName, c.isoCode as countryIsoCode) " +
            "FROM PlaceEntity p " +
            "JOIN ProvinceEntity pr ON p.provinceId = pr.provinceId " +
            "JOIN CountryEntity c ON pr.countryId = c.countryId " +
            "WHERE p.placeId = :placeId")
    Optional<PlaceWithHierarchyProjection> findByIdWithHierarchy(UUID placeId);

    @Query("SELECT new org.school.management.geography.infra.persistence.entity.PlaceWithHierarchyProjection(" +
            "p.placeId, p.name as placeName, p.type, p.postalCode, " +
            "pr.provinceId, pr.name as provinceName, pr.code, " +
            "c.countryId, c.name as countryName, c.isoCode as countryIsoCode) " +
            "FROM PlaceEntity p " +
            "JOIN ProvinceEntity pr ON p.provinceId = pr.provinceId " +
            "JOIN CountryEntity c ON pr.countryId = c.countryId " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(pr.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY p.name")
    List<PlaceWithHierarchyProjection> globalSearch(String searchTerm);
}

