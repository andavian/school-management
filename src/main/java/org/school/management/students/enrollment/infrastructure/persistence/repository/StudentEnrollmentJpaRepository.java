package org.school.management.students.enrollment.infrastructure.persistence.repository;

import org.school.management.students.enrollment.infrastructure.persistence.entity.StudentEnrollmentEntity;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentEnrollmentJpaRepository
        extends JpaRepository<StudentEnrollmentEntity, UUID> {

    Optional<StudentEnrollmentEntity> findByStudentIdAndAcademicYearId(
            UUID studentId,
            UUID academicYearId
    );

    List<StudentEnrollmentEntity> findAllByStudentId(UUID studentId);

    List<StudentEnrollmentEntity> findAllByAcademicYearId(UUID academicYearId);

    List<StudentEnrollmentEntity> findByGradeLevelIdAndAcademicYearId(
            UUID gradeLevelId,
            UUID academicYearId
    );

    @Query("""
            SELECT e FROM StudentEnrollmentEntity e
            WHERE e.studentId = :studentId
            AND e.status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')
            """)
    List<StudentEnrollmentEntity> findActiveByStudentId(@Param("studentId") UUID studentId);

    @Query("""
            SELECT COUNT(e) > 0 FROM StudentEnrollmentEntity e
            WHERE e.studentId = :studentId
            AND e.academicYearId = :academicYearId
            AND e.status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')
            """)
    boolean existsActiveEnrollment(
            @Param("studentId") UUID studentId,
            @Param("academicYearId") UUID academicYearId
    );

    @Query("""
            SELECT COUNT(e) > 0 FROM StudentEnrollmentEntity e
            WHERE e.studentId = :studentId
            AND e.academicYearId = :academicYearId
            AND e.status = 'COMPLETED'
            """)
    boolean existsCompletedEnrollment(
            @Param("studentId") UUID studentId,
            @Param("academicYearId") UUID academicYearId
    );
}