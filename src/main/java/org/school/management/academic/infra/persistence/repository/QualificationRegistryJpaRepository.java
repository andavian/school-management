package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.QualificationRegistryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QualificationRegistryJpaRepository extends JpaRepository<QualificationRegistryEntity, UUID> {
    Optional<QualificationRegistryEntity> findByRegistryNumber(String registryNumber);


    @Query("""
            SELECT qr FROM QualificationRegistryEntity qr
            WHERE qr.academicYearId = :academicYearId
            AND qr.status = 'ACTIVE'
            """)
    Optional<QualificationRegistryEntity> findActiveByAcademicYear(
            @Param("academicYearId") UUID academicYearId
    );

    List<QualificationRegistryEntity> findByAcademicYearId(UUID academicYearId);

    List<QualificationRegistryEntity> findByStatus(String status);

    @Query("SELECT qr FROM QualificationRegistryEntity qr " +
            "JOIN AcademicYearEntity ay ON qr.academicYearId = ay.academicYearId " +
            "WHERE ay.status = 'ACTIVE' AND qr.status = 'ACTIVE'")
    Optional<QualificationRegistryEntity> findActiveInCurrentYear();

    boolean existsByRegistryNumber(String registryNumber);

    long countByAcademicYearId(UUID academicYearId);

    @Query("SELECT COUNT(qr) FROM QualificationRegistryEntity qr WHERE qr.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT qr FROM QualificationRegistryEntity qr WHERE qr.status = 'ACTIVE' " +
            "AND (qr.maxFolios - qr.currentFolio) < :threshold")
    List<QualificationRegistryEntity> findNearFullRegistries(@Param("threshold") Integer threshold);

    @Modifying
    @Query("UPDATE QualificationRegistryEntity qr SET qr.currentFolio = qr.currentFolio + 1 " +
            "WHERE qr.registryId = :id AND qr.currentFolio < qr.endFolio")
    int incrementFolio(@Param("id") UUID id);

    @Query("SELECT (qr.endFolio - qr.currentFolio + 1) FROM QualificationRegistryEntity qr " +
            "WHERE qr.registryId = :id")
    Integer getAvailableFolios(@Param("id") UUID id);
}
