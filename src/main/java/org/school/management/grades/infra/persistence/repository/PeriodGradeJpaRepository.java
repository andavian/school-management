package org.school.management.grades.infra.persistence.repository;

import org.school.management.grades.infra.persistence.entity.PeriodGradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PeriodGradeJpaRepository extends JpaRepository<PeriodGradeEntity, UUID> {

    List<PeriodGradeEntity> findByStudentCourseSubjectId(UUID studentCourseSubjectId);

    Optional<PeriodGradeEntity> findByStudentCourseSubjectIdAndPeriodId(
            UUID studentCourseSubjectId, UUID periodId
    );

    @Query("""
            SELECT pg FROM PeriodGradeEntity pg
            WHERE pg.periodId = :periodId
            AND pg.isValidated = false
            """)
    List<PeriodGradeEntity> findUnvalidatedByPeriod(@Param("periodId") UUID periodId);

    @Query("""
            SELECT pg FROM PeriodGradeEntity pg
            WHERE pg.studentCourseSubjectId IN :studentCourseSubjectIds
            AND pg.isValidated = true
            ORDER BY pg.periodId
            """)
    List<PeriodGradeEntity> findValidatedGrades(
            @Param("studentCourseSubjectIds") List<UUID> studentCourseSubjectIds
    );

    @Query("""
            SELECT pg FROM PeriodGradeEntity pg
            JOIN StudentCourseSubjectEntity scs ON pg.studentCourseSubjectId = scs.studentCourseSubjectId
            WHERE scs.enrollmentId = :enrollmentId
            ORDER BY pg.periodId
            """)
    List<PeriodGradeEntity> findByEnrollment(@Param("enrollmentId") UUID enrollmentId);

    boolean existsByStudentCourseSubjectIdAndPeriodId(
            UUID studentCourseSubjectId, UUID periodId
    );
}
