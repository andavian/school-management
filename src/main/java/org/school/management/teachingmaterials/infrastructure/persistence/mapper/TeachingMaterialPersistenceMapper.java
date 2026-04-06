package org.school.management.teachingmaterials.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.valueobject.TeachingMaterialId;
import org.school.management.teachingmaterials.infrastructure.persistence.entity.TeachingMaterialEntity;

/**
 * Persistence Mapper: TeachingMaterial (domain) ↔ TeachingMaterialEntity (JPA).
 *
 * Usa default methods para VOs compuestos — patrón establecido en teachers/parents.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeachingMaterialPersistenceMapper {

    default TeachingMaterialEntity toEntity(TeachingMaterial domain) {
        if (domain == null) return null;

        TeachingMaterialEntity entity = new TeachingMaterialEntity();
        entity.setMaterialId(domain.getMaterialId().value());
        entity.setTeacherId(domain.getTeacherId().value());
        entity.setCourseSubjectId(domain.getCourseSubjectId().value());
        entity.setSubjectId(domain.getSubjectId().value());
        entity.setAcademicYearId(domain.getAcademicYearId().value());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setMaterialType(domain.getMaterialType());
        entity.setFilePath(domain.getFilePath());
        entity.setFileName(domain.getFileName());
        entity.setFileSizeBytes(domain.getFileSizeBytes());
        entity.setMimeType(domain.getMimeType());
        entity.setVisibleToStudents(domain.isVisibleToStudents());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default TeachingMaterial toDomain(TeachingMaterialEntity entity) {
        if (entity == null) return null;

        return TeachingMaterial.builder()
                .materialId(TeachingMaterialId.of(entity.getMaterialId()))
                .teacherId(TeacherId.from(entity.getTeacherId()))
                .courseSubjectId(CourseSubjectId.of(entity.getCourseSubjectId()))
                .subjectId(SubjectId.of(entity.getSubjectId()))
                .academicYearId(AcademicYearId.of(entity.getAcademicYearId()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .materialType(entity.getMaterialType())
                .filePath(entity.getFilePath())
                .fileName(entity.getFileName())
                .fileSizeBytes(entity.getFileSizeBytes())
                .mimeType(entity.getMimeType())
                .visibleToStudents(entity.isVisibleToStudents())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}