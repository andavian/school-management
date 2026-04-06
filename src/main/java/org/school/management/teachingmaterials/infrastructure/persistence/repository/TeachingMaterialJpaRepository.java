package org.school.management.teachingmaterials.infrastructure.persistence.repository;

import org.school.management.teachingmaterials.infrastructure.persistence.entity.TeachingMaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeachingMaterialJpaRepository extends JpaRepository<TeachingMaterialEntity, UUID> {

    List<TeachingMaterialEntity> findByCourseSubjectId(UUID courseSubjectId);

    List<TeachingMaterialEntity> findByTeacherId(UUID teacherId);

    List<TeachingMaterialEntity> findByTeacherIdAndCourseSubjectId(UUID teacherId, UUID courseSubjectId);

    @Query("SELECT m FROM TeachingMaterialEntity m " +
            "WHERE m.courseSubjectId IN :courseSubjectIds " +
            "AND m.visibleToStudents = true")
    List<TeachingMaterialEntity> findVisibleByCourseSubjectIds(
            @Param("courseSubjectIds") List<UUID> courseSubjectIds);
}