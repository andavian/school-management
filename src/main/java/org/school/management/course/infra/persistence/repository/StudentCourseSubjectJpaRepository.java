package org.school.management.course.infra.persistence.repository;

import org.school.management.course.infra.persistence.entity.StudentCourseSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentCourseSubjectJpaRepository extends JpaRepository<StudentCourseSubjectEntity, UUID> {

    List<StudentCourseSubjectEntity> findByEnrollmentId(UUID enrollmentId);

    List<StudentCourseSubjectEntity> findByCourseSubjectId(UUID courseSubjectId);

    Optional<StudentCourseSubjectEntity> findByEnrollmentIdAndCourseSubjectId(
            UUID enrollmentId, UUID courseSubjectId
    );

    @Query("""
            SELECT scs FROM StudentCourseSubjectEntity scs
            WHERE scs.enrollmentId = :enrollmentId
            AND scs.status = :status
            """)
    List<StudentCourseSubjectEntity> findByEnrollmentAndStatus(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("status") String status
    );

    @Query("""
            SELECT scs FROM StudentCourseSubjectEntity scs
            WHERE scs.enrollmentId = :enrollmentId
            AND scs.attendancePercentage < :minPercentage
            """)
    List<StudentCourseSubjectEntity> findWithLowAttendance(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("minPercentage") java.math.BigDecimal minPercentage
    );

    @Query("""
            SELECT COUNT(scs) FROM StudentCourseSubjectEntity scs
            WHERE scs.courseSubjectId = :courseSubjectId
            AND scs.status IN ('ENROLLED', 'ATTENDING')
            """)
    long countActiveStudents(@Param("courseSubjectId") UUID courseSubjectId);

    boolean existsByEnrollmentIdAndCourseSubjectId(UUID enrollmentId, UUID courseSubjectId);
}
