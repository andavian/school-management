package org.school.management.students.records.domain.repository;

import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;

import java.util.List;
import java.util.Optional;

public interface DocumentTypeRepository {

    Optional<DocumentType> findById(DocumentTypeId id);

    List<DocumentType> findAll();

    List<DocumentType> findAllActive();

    List<DocumentType> findByCategory(DocumentCategory category);

    List<DocumentType> findActiveByCategoryAndMandatory(DocumentCategory category, Boolean mandatory);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    DocumentType save(DocumentType documentType);
}