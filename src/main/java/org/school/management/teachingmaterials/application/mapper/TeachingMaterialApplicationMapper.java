package org.school.management.teachingmaterials.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;

/**
 * Mapper MapStruct: TeachingMaterial (domain) → Application DTOs.
 *
 * Solo mapea domain → response. El request → domain lo orquesta el Use Case
 * via {@link TeachingMaterial#create(...)}.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeachingMaterialApplicationMapper {

    @Mapping(target = "materialId",       expression = "java(material.getMaterialId().value())")
    @Mapping(target = "teacherId",        expression = "java(material.getTeacherId().value())")
    @Mapping(target = "courseSubjectId",  expression = "java(material.getCourseSubjectId().value())")
    @Mapping(target = "subjectId",        expression = "java(material.getSubjectId().value())")
    @Mapping(target = "academicYearId",   expression = "java(material.getAcademicYearId().value())")
    @Mapping(target = "publicUrl",        source = "fileName")   // fileName = URL pública OCI
    @Mapping(target = "title",            source = "title")
    @Mapping(target = "description",      source = "description")
    @Mapping(target = "materialType",     source = "materialType")
    @Mapping(target = "fileSizeBytes",    source = "fileSizeBytes")
    @Mapping(target = "mimeType",         source = "mimeType")
    @Mapping(target = "visibleToStudents",source = "visibleToStudents")
    @Mapping(target = "createdAt",        source = "createdAt")
    @Mapping(target = "updatedAt",        source = "updatedAt")
    TeachingMaterialResponse toResponse(TeachingMaterial material);
}