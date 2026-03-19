package org.school.management.course.infrastructure.persistence.repository;

import org.school.management.course.infrastructure.persistence.entity.CourseSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseSubjectJpaRepository extends JpaRepository<CourseSubjectEntity, UUID> {

    List<CourseSubjectEntity> findByGradeLevelIdAndAcademicYearId(
            UUID gradeLevelId, UUID academicYearId);

    List<CourseSubjectEntity> findByTeacherIdAndAcademicYearId(
            UUID teacherId, UUID academicYearId);

    boolean existsByGradeLevelIdAndSubjectIdAndAcademicYearId(
            UUID gradeLevelId, UUID subjectId, UUID academicYearId);
}