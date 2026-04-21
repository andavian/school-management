package org.school.management.students.records.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.school.management.students.records.infrastructure.persistence.entity.DocumentTypeEntity;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DocumentTypePersistenceMapper {

    default DocumentTypeEntity toEntity(DocumentType domain) {
        DocumentTypeEntity entity = new DocumentTypeEntity();
        entity.setDocumentTypeId(domain.getDocumentTypeId().value());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        entity.setMandatory(domain.isMandatory());
        entity.setCategory(domain.getCategory().name());
        entity.setValidForYears(domain.getValidForYears());
        entity.setActive(domain.isActive());
        return entity;
    }

    default DocumentType toDomain(DocumentTypeEntity entity) {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.of(entity.getDocumentTypeId()))
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .isMandatory(entity.isMandatory())
                .category(DocumentCategory.valueOf(entity.getCategory()))
                .validForYears(entity.getValidForYears())
                .isActive(entity.isActive())
                .build();
    }
}