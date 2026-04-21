package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.request.DocumentTypeRequest.CreateDocumentTypeRequest;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.exception.DocumentTypeAlreadyExistsException;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateDocumentTypeUseCase {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeApplicationMapper mapper;

    public DocumentTypeResponse execute(CreateDocumentTypeRequest request) {
        log.debug("CreateDocumentType — code: {}", request.code());

        if (documentTypeRepository.existsByCode(request.code())) {
            throw DocumentTypeAlreadyExistsException.withCode(request.code());
        }

        if (documentTypeRepository.existsByName(request.name())) {
            throw DocumentTypeAlreadyExistsException.withName(request.name());
        }

        DocumentType documentType = DocumentType.builder()
                .documentTypeId(DocumentTypeId.generate())
                .name(request.name())
                .code(request.code())
                .category(request.category())
                .isMandatory(request.mandatory())
                .description(request.description())
                .validForYears(request.validForYears())
                .isActive(true)
                .build();

        DocumentType saved = documentTypeRepository.save(documentType);
        log.info("DocumentType created — id: {}, code: {}",
                saved.getDocumentTypeId().value(), saved.getCode());

        return mapper.toResponse(saved);
    }
}