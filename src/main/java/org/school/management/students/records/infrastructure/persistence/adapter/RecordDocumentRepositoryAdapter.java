package org.school.management.students.records.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.repository.RecordDocumentRepository;
import org.school.management.students.records.domain.valueobject.DocumentId;
import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.school.management.students.records.infrastructure.persistence.entity.RecordDocumentEntity;
import org.school.management.students.records.infrastructure.persistence.mapper.StudentRecordPersistenceMapper;
import org.school.management.students.records.infrastructure.persistence.repository.RecordDocumentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecordDocumentRepositoryAdapter implements RecordDocumentRepository {

    private final RecordDocumentJpaRepository jpaRepository;
    private final StudentRecordPersistenceMapper mapper;

    @Override
    public RecordDocument save(RecordDocument document) {
        RecordDocumentEntity entity = mapper.toDocumentEntity(document);
        RecordDocumentEntity saved = jpaRepository.save(entity);
        return mapper.toDocumentDomain(saved);
    }

    @Override
    public Optional<RecordDocument> findById(DocumentId documentId) {
        return jpaRepository.findById(documentId.value())
                .map(mapper::toDocumentDomain);
    }

    @Override
    public Optional<RecordDocument> findByIdAndRecordId(DocumentId documentId, RecordId recordId) {
        return jpaRepository
                .findByDocumentIdAndRecordId(documentId.value(), recordId.value())
                .map(mapper::toDocumentDomain);
    }

    @Override
    public List<RecordDocument> findAllByRecordId(RecordId recordId) {
        return jpaRepository.findAllByRecordId(recordId.value())
                .stream()
                .map(mapper::toDocumentDomain)
                .toList();
    }

    @Override
    public List<RecordDocument> findAllByRecordIdAndStatus(RecordId recordId,
                                                           DocumentStatus status) {
        return jpaRepository
                .findAllByRecordIdAndStatus(recordId.value(), status)
                .stream()
                .map(mapper::toDocumentDomain)
                .toList();
    }

    @Override
    public void delete(DocumentId documentId, RecordId recordId) {
        jpaRepository.deleteByDocumentIdAndRecordId(documentId.value(), recordId.value());
    }
}