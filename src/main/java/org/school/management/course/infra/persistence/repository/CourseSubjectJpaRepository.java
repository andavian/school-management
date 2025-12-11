package org.school.management.course.infra.persistence.repository;

import org.school.management.course.infra.persistence.entity.CourseSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseSubjectJpaRepository extends JpaRepository<CourseSubjectEntity, UUID> {

    List<CourseSubjectEntity> findByGradeLevelId(UUID gradeLevelId);

    List<CourseSubjectEntity> findByTeacherId(UUID teacherId);

    List<CourseSubjectEntity> findByAcademicYearId(UUID academicYearId);

    @Query("""
            SELECT cs FROM CourseSubjectEntity cs
            WHERE cs.gradeLevelId = :gradeLevelId
            AND cs.academicYearId = :academicYearId
            AND cs.status = 'ACTIVE'
            ORDER BY cs.subjectId
            """)
    List<CourseSubjectEntity> findActiveByGradeLevelAndYear(
            @Param("gradeLevelId") UUID gradeLevelId,
            @Param("academicYearId") UUID academicYearId
    );

    Optional<CourseSubjectEntity> findByGradeLevelIdAndSubjectIdAndAcademicYearId(
            UUID gradeLevelId, UUID subjectId, UUID academicYearId
    );

    @Query("""
            SELECT cs FROM CourseSubjectEntity cs
            WHERE cs.teacherId = :teacherId
            AND cs.academicYearId = :academicYearId
            AND cs.status = 'ACTIVE'
            """)
    List<CourseSubjectEntity> findTeacherCourses(
            @Param("teacherId") UUID teacherId,
            @Param("academicYearId") UUID academicYearId
    );

    boolean existsByGradeLevelIdAndSubjectIdAndAcademicYearId(
            UUID gradeLevelId, UUID subjectId, UUID academicYearId
    );

    long countByTeacherId(UUID teacherId);
}
