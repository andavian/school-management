package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.SubjectEntity;
import org.school.management.academic.infra.persistence.entity.SubjectWithOrientationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectJpaRepository extends JpaRepository<SubjectEntity, UUID> {
    Optional<SubjectEntity> findByCode(String code);

    List<SubjectEntity> findByIsActiveTrue();

    List<SubjectEntity> findByYearLevel(Integer yearLevel);

    @Query("""
            SELECT s FROM SubjectEntity s
            WHERE s.yearLevel = :yearLevel
            AND s.orientationId IS NULL
            AND s.isCurrent = true
            ORDER BY s.name
            """)
    List<SubjectEntity> findCommonSubjects(@Param("yearLevel") Integer yearLevel);

    List<SubjectEntity> findByOrientationId(UUID orientationId);

    List<SubjectEntity> findByYearLevelAndOrientationIdIsNull(Integer yearLevel);

    @Query("""
            SELECT s FROM SubjectEntity s
            WHERE s.yearLevel = :yearLevel
            AND (s.orientationId = :orientationId OR s.orientationId IS NULL)
            AND s.isCurrent = true
            ORDER BY s.isMandatory DESC, s.name
            """)
    List<SubjectEntity> findByYearLevelAndOrientation(
            @Param("yearLevel") Integer yearLevel,
            @Param("orientationId") UUID orientationId
    );

    @Query("SELECT s FROM SubjectEntity s WHERE s.yearLevel = :yearLevel " +
            "AND (s.orientationId = :orientationId OR s.orientationId IS NULL) " +
            "AND s.isCurrent = true")
    List<SubjectEntity> findAvailableForGradeLevel(
            @Param("yearLevel") Integer yearLevel,
            @Param("orientationId") UUID orientationId
    );

    boolean existsByCode(String code);

    long countByYearLevel(Integer yearLevel);

    long countByOrientationId(UUID orientationId);

    @Query("SELECT s FROM SubjectEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<SubjectEntity> search(@Param("search") String search);

    // Query con orientación
    @Query("SELECT new org.school.management.academic.infra.persistence.entity.SubjectWithOrientationProjection(" +
            "s.subjectId, s.name, s.code, s.yearLevel, s.weeklyHours, s.isCurrent, " +
            "o.orientationId, o.name, o.code) " +
            "FROM SubjectEntity s " +
            "LEFT JOIN OrientationEntity o ON s.orientationId = o.orientationId " +
            "WHERE s.subjectId = :id")
    Optional<SubjectWithOrientationProjection> findByIdWithOrientation(@Param("id") UUID id);

    @Query("SELECT new org.school.management.academic.infra.persistence.entity.SubjectWithOrientationProjection(" +
            "s.subjectId, s.name, s.code, s.yearLevel, s.weeklyHours, s.isCurrent, " +
            "o.orientationId, o.name, o.code) " +
            "FROM SubjectEntity s " +
            "LEFT JOIN OrientationEntity o ON s.orientationId = o.orientationId " +
            "WHERE s.yearLevel = :yearLevel AND s.isCurrent = true " +
            "ORDER BY s.name")
    List<SubjectWithOrientationProjection> findByYearLevelWithOrientation(@Param("yearLevel") Integer yearLevel);
}
