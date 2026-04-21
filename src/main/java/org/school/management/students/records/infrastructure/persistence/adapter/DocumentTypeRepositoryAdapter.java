package org.school.management.students.records.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.school.management.students.records.infrastructure.persistence.mapper.DocumentTypePersistenceMapper;
import org.school.management.students.records.infrastructure.persistence.repository.DocumentTypeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentTypeRepositoryAdapter implements DocumentTypeRepository {

    private final DocumentTypeJpaRepository jpaRepository;
    private final DocumentTypePersistenceMapper mapper;

    @Override
    public Optional<DocumentType> findById(DocumentTypeId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<DocumentType> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<DocumentType> findAllActive() {
        return jpaRepository.findAllByActiveTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<DocumentType> findByCategory(DocumentCategory category) {
        return jpaRepository.findAllByCategory(category.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<DocumentType> findActiveByCategoryAndMandatory(
            DocumentCategory category,
            Boolean mandatory) {
        return jpaRepository.findActiveByCategoryAndMandatory(category.name(), mandatory).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public DocumentType save(DocumentType documentType) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(documentType))
        );
    }
}