package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.exception.DocumentNotFoundException;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.records.domain.valueobject.DocumentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Aprueba o rechaza un documento individual dentro del legajo.
 * La acción debe ser "APPROVE" o "REJECT".
 * La validación de estado del documento vive en el dominio (RecordDocument).
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewDocumentUseCase {

    private final StudentRecordRepository recordRepository;
    private final GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;

    public StudentRecordResponse execute(
            UUID studentId,
            UUID documentId,
            String action,
            String observations) {

        log.debug("Reviewing document — studentId: {}, documentId: {}, action: {}",
                studentId, documentId, action);

        // Obtener el legajo
        StudentRecord record = recordRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .orElseThrow(() -> RecordNotFoundException.byStudentId(studentId));

        // Obtener el documento dentro del agregado
        RecordDocument document = record
                .getDocument(DocumentId.of(documentId))
                .orElseThrow(() -> DocumentNotFoundException.byId(documentId));

        // Delegar al dominio según la acción
        switch (action.toUpperCase()) {
            case "APPROVE" -> {
                document.approve(observations);
                log.info("Document approved — documentId: {}", documentId);
            }
            case "REJECT" -> {
                document.reject(observations);
                log.info("Document rejected — documentId: {}", documentId);
            }
            default -> throw new IllegalArgumentException(
                    "Invalid document action: " + action + ". Expected APPROVE or REJECT"
            );
        }

        StudentRecord saved = recordRepository.save(record);
        return getRecordByStudentIdUseCase.buildResponse(saved);
    }
}