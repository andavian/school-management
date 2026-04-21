package org.school.management.students.records.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.domain.model.DocumentType;

@Mapper(componentModel = "spring")
public interface DocumentTypeApplicationMapper {

    @Mapping(target = "documentTypeId",  expression = "java(domain.getDocumentTypeId().value())")
    @Mapping(target = "mandatory",       source = "mandatory")
    @Mapping(target = "permanent",       expression = "java(domain.isPermanent())")
    @Mapping(target = "active",          source = "active")
    DocumentTypeResponse toResponse(DocumentType domain);
}