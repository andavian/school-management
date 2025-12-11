package org.school.management.grades.infra.persistence.repository;

import org.school.management.grades.infra.persistence.entity.FinalGradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinalGradeJpaRepository extends JpaRepository<FinalGradeEntity, UUID> {

    Optional<FinalGradeEntity> findByStudentCourseSubjectIdAndAcademicYearId(
            UUID studentCourseSubjectId, UUID academicYearId
    );

    List<FinalGradeEntity> findByAcademicYearId(UUID academicYearId);

    @Query("""
            SELECT fg FROM FinalGradeEntity fg
            JOIN StudentCourseSubjectEntity scs ON fg.studentCourseSubjectId = scs.studentCourseSubjectId
            WHERE scs.enrollmentId = :enrollmentId
            AND fg.academicYearId = :academicYearId
            """)
    List<FinalGradeEntity> findByEnrollmentAndYear(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("academicYearId") UUID academicYearId
    );

    @Query("""
            SELECT fg FROM FinalGradeEntity fg
            WHERE fg.academicYearId = :academicYearId
            AND fg.isValidated = false
            """)
    List<FinalGradeEntity> findUnvalidatedByYear(@Param("academicYearId") UUID academicYearId);

    @Query("""
            SELECT fg FROM FinalGradeEntity fg
            WHERE fg.academicYearId = :academicYearId
            AND fg.isValidated = true
            AND fg.recordedInRegistry = false
            """)
    List<FinalGradeEntity> findPendingRegistryRecord(@Param("academicYearId") UUID academicYearId);

    @Query("""
            SELECT fg FROM FinalGradeEntity fg
            WHERE fg.registryId = :registryId
            ORDER BY fg.folioNumber
            """)
    List<FinalGradeEntity> findByRegistry(@Param("registryId") UUID registryId);

    @Query("""
            SELECT fg FROM FinalGradeEntity fg
            WHERE fg.academicYearId = :academicYearId
            AND fg.status = :status
            """)
    List<FinalGradeEntity> findByYearAndStatus(
            @Param("academicYearId") UUID academicYearId,
            @Param("status") String status
    );

    @Query("""
            SELECT COUNT(fg) FROM FinalGradeEntity fg
            WHERE fg.academicYearId = :academicYearId
            AND fg.status = 'PASSED'
            """)
    long countPassedByYear(@Param("academicYearId") UUID academicYearId);

    @Query("""
            SELECT COUNT(fg) FROM FinalGradeEntity fg
            WHERE fg.academicYearId = :academicYearId
            AND fg.status = 'FAILED'
            """)
    long countFailedByYear(@Param("academicYearId") UUID academicYearId);

    boolean existsByStudentCourseSubjectIdAndAcademicYearId(
            UUID studentCourseSubjectId, UUID academicYearId
    );
}
