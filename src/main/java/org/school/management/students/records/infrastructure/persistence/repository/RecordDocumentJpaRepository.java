package org.school.management.students.records.infrastructure.persistence.repository;

import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.infrastructure.persistence.entity.RecordDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecordDocumentJpaRepository
        extends JpaRepository<RecordDocumentEntity, UUID> {

    List<RecordDocumentEntity> findAllByRecordId(UUID recordId);

    Optional<RecordDocumentEntity> findByDocumentIdAndRecordId(
            UUID documentId,
            UUID recordId
    );

    List<RecordDocumentEntity> findAllByRecordIdAndStatus(
            UUID recordId,
            DocumentStatus status
    );

    void deleteByDocumentIdAndRecordId(UUID documentId, UUID recordId);
}