package org.school.management.students.records.domain.repository;

import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.valueobject.DocumentId;
import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.domain.valueobject.RecordId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para persistencia de {@link RecordDocument}.
 *
 * <p>RecordDocument no es un agregado root — pertenece a {@code StudentRecord}.
 * Este repositorio existe para permitir operaciones directas sobre documentos
 * sin cargar el agregado completo (ej: aprobar/rechazar un documento individual).</p>
 */
public interface RecordDocumentRepository {

    RecordDocument save(RecordDocument document);

    Optional<RecordDocument> findById(DocumentId documentId);

    Optional<RecordDocument> findByIdAndRecordId(DocumentId documentId, RecordId recordId);

    List<RecordDocument> findAllByRecordId(RecordId recordId);

    List<RecordDocument> findAllByRecordIdAndStatus(RecordId recordId, DocumentStatus status);

    void delete(DocumentId documentId, RecordId recordId);
}