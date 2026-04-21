package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.request.DocumentTypeRequest.UpdateDocumentTypeRequest;
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
public class UpdateDocumentTypeUseCase {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeApplicationMapper mapper;

    public DocumentTypeResponse execute(UUID documentTypeId, UpdateDocumentTypeRequest request) {
        log.debug("UpdateDocumentType — id: {}", documentTypeId);

        DocumentType documentType = documentTypeRepository
                .findById(DocumentTypeId.of(documentTypeId))
                .orElseThrow(() -> DocumentTypeNotFoundException.byId(documentTypeId));

        documentType.update(
                request.name(),
                request.category(),
                request.mandatory(),
                request.description(),
                request.validForYears()
        );

        DocumentType saved = documentTypeRepository.save(documentType);
        log.info("DocumentType updated — id: {}", documentTypeId);

        return mapper.toResponse(saved);
    }
}