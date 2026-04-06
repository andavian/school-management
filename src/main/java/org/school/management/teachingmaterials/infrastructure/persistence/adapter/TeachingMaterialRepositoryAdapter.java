package org.school.management.teachingmaterials.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.school.management.teachingmaterials.domain.valueobject.TeachingMaterialId;
import org.school.management.teachingmaterials.infrastructure.persistence.mapper.TeachingMaterialPersistenceMapper;
import org.school.management.teachingmaterials.infrastructure.persistence.repository.TeachingMaterialJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeachingMaterialRepositoryAdapter implements TeachingMaterialRepository {

    private final TeachingMaterialJpaRepository jpaRepository;
    private final TeachingMaterialPersistenceMapper mapper;

    @Override
    public TeachingMaterial save(TeachingMaterial material) {
        var entity = mapper.toEntity(material);
        var saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TeachingMaterial> findById(TeachingMaterialId materialId) {
        return jpaRepository.findById(materialId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<TeachingMaterial> findByCourseSubjectId(CourseSubjectId courseSubjectId) {
        return jpaRepository.findByCourseSubjectId(courseSubjectId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TeachingMaterial> findVisibleByCourseSubjectIds(List<CourseSubjectId> courseSubjectIds) {
        if (courseSubjectIds == null || courseSubjectIds.isEmpty()) return List.of();

        List<java.util.UUID> ids = courseSubjectIds.stream()
                .map(CourseSubjectId::value)
                .toList();
        return jpaRepository.findVisibleByCourseSubjectIds(ids)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TeachingMaterial> findByTeacherId(TeacherId teacherId) {
        return jpaRepository.findByTeacherId(teacherId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TeachingMaterial> findByTeacherIdAndCourseSubjectId(TeacherId teacherId,
                                                                    CourseSubjectId courseSubjectId) {
        return jpaRepository.findByTeacherIdAndCourseSubjectId(
                        teacherId.value(), courseSubjectId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(TeachingMaterialId materialId) {
        jpaRepository.deleteById(materialId.value());
    }

    @Override
    public boolean existsById(TeachingMaterialId materialId) {
        return jpaRepository.existsById(materialId.value());
    }
}