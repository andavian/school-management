package org.school.management.course.infrastructure.persistence.repository;

import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;
import org.school.management.course.infrastructure.persistence.entity.StudentCourseSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentCourseSubjectJpaRepository extends JpaRepository<StudentCourseSubjectEntity, UUID> {

    List<StudentCourseSubjectEntity> findByEnrollmentId(UUID enrollmentId);

    Optional<StudentCourseSubjectEntity> findByEnrollmentIdAndCourseSubjectId(
            UUID enrollmentId, UUID courseSubjectId);

    List<StudentCourseSubjectEntity> findByEnrollmentIdAndStatus(
            UUID enrollmentId, SubjectEnrollmentStatus status);

    boolean existsByEnrollmentIdAndCourseSubjectId(UUID enrollmentId, UUID courseSubjectId);
}