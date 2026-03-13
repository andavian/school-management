package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.records.application.dto.request.UpdateRecordStatusRequest;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Gestiona el workflow completo del legajo:
 *  - SUBMIT: envía a revisión (valida documentos obligatorios)
 *  - APPROVE: aprueba el legajo completo
 *  - REJECT: rechaza el legajo con motivo
 *
 * La validación de estados terminales vive en el dominio (StudentRecord).
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateRecordStatusUseCase {

    private final StudentRecordRepository recordRepository;
    private final GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;

    public StudentRecordResponse execute(
            UUID studentId,
            UpdateRecordStatusRequest request,
            UUID reviewedByUserId) {

        log.debug("Updating record status — studentId: {}, action: {}",
                studentId, request.recordAction());

        StudentRecord record = recordRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .orElseThrow(() -> RecordNotFoundException.byStudentId(studentId));

        UserId reviewedBy = UserId.from(reviewedByUserId);

        switch (request.recordAction().toUpperCase()) {
            case "SUBMIT" -> {
                Set<DocumentTypeId> mandatoryTypes = resolveMandatoryTypes(
                        request.mandatoryDocumentTypeIds()
                );
                record.submitForReview(mandatoryTypes);
                log.info("Record submitted for review — studentId: {}", studentId);
            }
            case "APPROVE" -> {
                record.approve(reviewedBy, request.recordObservations());
                log.info("Record approved — studentId: {}, reviewedBy: {}",
                        studentId, reviewedByUserId);
            }
            case "REJECT" -> {
                record.reject(reviewedBy, request.recordObservations());
                log.info("Record rejected — studentId: {}, reviewedBy: {}",
                        studentId, reviewedByUserId);
            }
            default -> throw new IllegalArgumentException(
                    "Invalid record action: " + request.recordAction()
                            + ". Expected SUBMIT, APPROVE or REJECT"
            );
        }

        StudentRecord saved = recordRepository.save(record);
        return getRecordByStudentIdUseCase.buildResponse(saved);
    }

    private Set<DocumentTypeId> resolveMandatoryTypes(Set<UUID> typeIds) {
        if (typeIds == null || typeIds.isEmpty()) {
            return Set.of();
        }
        return typeIds.stream()
                .map(DocumentTypeId::of)
                .collect(Collectors.toSet());
    }
}