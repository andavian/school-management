package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.GradeLevelEntity;
import org.school.management.academic.infra.persistence.entity.GradeLevelWithDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeLevelJpaRepository extends JpaRepository<GradeLevelEntity, UUID> {
    List<GradeLevelEntity> findByAcademicYearId(UUID academicYearId);

    List<GradeLevelEntity> findByAcademicYearIdAndIsActiveTrue(UUID academicYearId);

    Optional<GradeLevelEntity> findByAcademicYearIdAndYearLevelAndDivision(
            UUID academicYearId,
            Integer yearLevel,
            String division
    );

    @Query("""
            SELECT gl FROM GradeLevelEntity gl
            WHERE gl.academicYearId = :academicYearId
            AND gl.yearLevel = :yearLevel
            ORDER BY gl.division
            """)
    List<GradeLevelEntity> findByAcademicYearAndYearLevel(
            @Param("academicYearId") UUID academicYearId,
            @Param("yearLevel") Integer yearLevel
    );

    List<GradeLevelEntity> findByOrientationId(UUID orientationId);

    List<GradeLevelEntity> findByIsActiveTrue();

    List<GradeLevelEntity> findByAcademicYearIdAndShift(UUID academicYearId, String shift);

    List<GradeLevelEntity> findByHomeroomTeacherId(UUID teacherId);

    @Query("""
            SELECT gl FROM GradeLevelEntity gl
            WHERE gl.academicYearId = :academicYearId
            AND gl.yearLevel = :yearLevel
            AND gl.division = :division
            """)
    Optional<GradeLevelEntity> findByYearLevelDivisionAndAcademicYear(
            @Param("academicYearId") UUID academicYearId,
            @Param("yearLevel") Integer yearLevel,
            @Param("division") String division
    );

    @Query("SELECT gl FROM GradeLevelEntity gl WHERE gl.academicYearId = :academicYearId " +
            "AND gl.yearLevel = :yearLevel AND gl.orientationId = :orientationId AND gl.isCurrent = true")
    List<GradeLevelEntity> findByYearLevelAndOrientation(
            @Param("academicYearId") UUID academicYearId,
            @Param("yearLevel") Integer yearLevel,
            @Param("orientationId") UUID orientationId
    );

    @Query("SELECT gl FROM GradeLevelEntity gl " +
            "JOIN AcademicYearEntity ay ON gl.academicYearId = ay.academicYearId " +
            "WHERE ay.status = 'ACTIVE' AND gl.isActive = true " +
            "ORDER BY gl.yearLevel, gl.division")
    List<GradeLevelEntity> findCurrentYearActiveLevels();

    boolean existsByAcademicYearIdAndYearLevelAndDivision(
            UUID academicYearId,
            Integer yearLevel,
            String division
    );

     long countByAcademicYearId(UUID academicYearId);

    long countByAcademicYearIdAndIsActiveTrue(UUID academicYearId);

    @Query("SELECT COUNT(gl) FROM GradeLevelEntity gl WHERE gl.homeroomTeacherId = :teacherId AND gl.isCurrent = true")
    long countActiveClassesByTeacher(@Param("teacherId") UUID teacherId);

    // Query con jerarquía completa (GradeLevel + AcademicYear + Orientation)
    @Query("SELECT new org.school.management.academic.infra.persistence.entity.GradeLevelWithDetailsProjection(" +
            "gl.gradeLevelId, gl.yearLevel, gl.division, gl.shift, gl.maxStudents, gl.homeroomTeacherId, " +
            "ay.academicYearId, ay.year, ay.status, " + // <-- 1. CAMBIAR 'isCurrent' por 'status'
            "o.orientationId, o.name, o.code) " +
            "FROM GradeLevelEntity gl JOIN AcademicYearEntity ay ON gl.academicYearId = ay.academicYearId " +
            "LEFT JOIN OrientationEntity o ON gl.orientationId = o.orientationId " +
            "WHERE ay.status = 'ACTIVE' AND gl.gradeLevelId = :id " +
            "AND gl.isActive = true ORDER BY gl.yearLevel, gl.division")
    Optional<GradeLevelWithDetailsProjection> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT new org.school.management.academic.infra.persistence.entity.GradeLevelWithDetailsProjection(" +
            "gl.gradeLevelId, gl.yearLevel, gl.division, gl.shift, gl.maxStudents, gl.homeroomTeacherId, " +
            "ay.academicYearId, ay.year, ay.status, " +
            "o.orientationId, o.name, o.code) " +
            "FROM GradeLevelEntity gl " +
            "JOIN AcademicYearEntity ay ON gl.academicYearId = ay.academicYearId " +
            "LEFT JOIN OrientationEntity o ON gl.orientationId = o.orientationId " +
            "WHERE ay.status = 'ACTIVE' " +
            "ORDER BY gl.yearLevel, gl.division")
    List<GradeLevelWithDetailsProjection> findAllCurrentWithDetails();
}
