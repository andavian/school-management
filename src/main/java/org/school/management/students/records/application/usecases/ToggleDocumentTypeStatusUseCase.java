package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.exception.DocumentTypeNotFoundException;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ToggleDocumentTypeStatusUseCase {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeApplicationMapper mapper;

    public DocumentTypeResponse execute(UUID documentTypeId, boolean activate) {
        log.debug("ToggleDocumentTypeStatus — id: {}, activate: {}", documentTypeId, activate);

        DocumentType documentType = documentTypeRepository
                .findById(DocumentTypeId.of(documentTypeId))
                .orElseThrow(() -> DocumentTypeNotFoundException.byId(documentTypeId));

        if (activate) {
            documentType.activate();
        } else {
            documentType.deactivate();
        }

        DocumentType saved = documentTypeRepository.save(documentType);
        log.info("DocumentType {} — id: {}",
                activate ? "activated" : "deactivated", documentTypeId);

        return mapper.toResponse(saved);
    }
}