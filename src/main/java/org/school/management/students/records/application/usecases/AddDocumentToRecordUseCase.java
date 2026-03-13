package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.records.application.dto.request.AddDocumentRequest;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.records.domain.valueobject.DocumentId;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Agrega un documento al legajo del estudiante.
 * Delega la validación de estado del legajo al dominio (StudentRecord.addDocument()).
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AddDocumentToRecordUseCase {

    private final StudentRecordRepository recordRepository;
    private final GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;

    public StudentRecordResponse execute(
            UUID studentId,
            AddDocumentRequest request,
            UUID uploadedByUserId) {

        log.debug("Adding document to record — studentId: {}, documentType: {}",
                studentId, request.documentTypeId());

        // Obtener el legajo del estudiante
        StudentRecord record = recordRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .orElseThrow(() -> RecordNotFoundException.byStudentId(studentId));

        // Construir el documento
        RecordDocument document = RecordDocument.builder()
                .documentId(DocumentId.generate())
                .recordId(RecordId.of(record.getRecordId().value()))
                .documentTypeId(DocumentTypeId.of(request.documentTypeId()))
                .title(request.title())
                .description(request.description())
                .filePath(request.filePath())
                .fileName(request.fileName())
                .fileSizeBytes(request.fileSizeBytes())
                .mimeType(request.mimeType())
                .issueDate(request.issueDate())
                .expiryDate(request.expiryDate())
                .issuingAuthority(request.issuingAuthority())
                .uploadedBy(UserId.from(uploadedByUserId))
                .build();

        // Delegar al agregado — valida estado del legajo
        record.addDocument(document);

        StudentRecord saved = recordRepository.save(record);
        log.info("Document added to record — studentId: {}, documentId: {}",
                studentId, document.getDocumentId().value());

        return getRecordByStudentIdUseCase.buildResponse(saved);
    }
}