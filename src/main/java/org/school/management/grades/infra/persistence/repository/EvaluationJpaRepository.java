package org.school.management.grades.infra.persistence.repository;

import org.school.management.grades.infra.persistence.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, UUID> {

    List<EvaluationEntity> findByStudentCourseSubjectId(UUID studentCourseSubjectId);

    @Query("""
            SELECT e FROM EvaluationEntity e
            WHERE e.studentCourseSubjectId = :studentCourseSubjectId
            AND e.periodId = :periodId
            ORDER BY e.evaluationDate
            """)
    List<EvaluationEntity> findByStudentCourseSubjectAndPeriod(
            @Param("studentCourseSubjectId") UUID studentCourseSubjectId,
            @Param("periodId") UUID periodId
    );

    @Query("""
            SELECT e FROM EvaluationEntity e
            WHERE e.studentCourseSubjectId = :studentCourseSubjectId
            AND e.periodId = :periodId
            AND e.isValidated = true
            ORDER BY e.evaluationDate
            """)
    List<EvaluationEntity> findValidatedEvaluations(
            @Param("studentCourseSubjectId") UUID studentCourseSubjectId,
            @Param("periodId") UUID periodId
    );

    @Query("""
            SELECT e FROM EvaluationEntity e
            WHERE e.periodId = :periodId
            AND e.evaluationDate BETWEEN :startDate AND :endDate
            AND e.status = :status
            """)
    List<EvaluationEntity> findByPeriodDateRangeAndStatus(
            @Param("periodId") UUID periodId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status
    );

    @Query("""
            SELECT e FROM EvaluationEntity e
            JOIN StudentCourseSubjectEntity scs ON e.studentCourseSubjectId = scs.studentCourseSubjectId
            JOIN CourseSubjectEntity cs ON scs.courseSubjectId = cs.courseSubjectId
            WHERE cs.teacherId = :teacherId
            AND e.status = 'GRADED'
            AND e.isValidated = false
            """)
    List<EvaluationEntity> findPendingValidationByTeacher(@Param("teacherId") UUID teacherId);

    @Query("""
            SELECT e FROM EvaluationEntity e
            WHERE e.studentCourseSubjectId IN :studentCourseSubjectIds
            AND e.isValidated = false
            """)
    List<EvaluationEntity> findUnvalidatedEvaluations(
            @Param("studentCourseSubjectIds") List<UUID> studentCourseSubjectIds
    );

    long countByStudentCourseSubjectIdAndPeriodId(UUID studentCourseSubjectId, UUID periodId);
}
