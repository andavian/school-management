package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyPlanJpaRepository extends JpaRepository<StudyPlanEntity, UUID> {

    Optional<StudyPlanEntity> findByCode(String code);

    List<StudyPlanEntity> findByYearLevelAndOrientationIdIsNull(Integer yearLevel);

    List<StudyPlanEntity> findByYearLevel(Integer yearLevel);

    List<StudyPlanEntity> findByOrientationId(UUID orientationId);

    Optional<StudyPlanEntity> findByYearLevelAndOrientationId(
            Integer yearLevel, UUID orientationId
    );

    List<StudyPlanEntity> findByIsActiveTrue();

    @Query("SELECT sp FROM StudyPlanEntity sp WHERE sp.yearLevel = :yearLevel " +
            "AND (sp.orientationId = :orientationId OR sp.orientationId IS NULL) " +
            "AND sp.isCurrent = true")
    List<StudyPlanEntity> findApplicableForGradeLevel(
            @Param("yearLevel") Integer yearLevel,
            @Param("orientationId") UUID orientationId
    );

    boolean existsByCode(String code);

    long countByYearLevel(Integer yearLevel);

    @Query("SELECT sp FROM StudyPlanEntity sp WHERE LOWER(sp.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(sp.code) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<StudyPlanEntity> search(@Param("search") String search);
}

