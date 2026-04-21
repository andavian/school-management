package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.exception.DocumentTypeNotFoundException;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetDocumentTypeByIdUseCase {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeApplicationMapper mapper;

    public DocumentTypeResponse execute(UUID documentTypeId) {
        log.debug("GetDocumentTypeById — id: {}", documentTypeId);

        return documentTypeRepository
                .findById(DocumentTypeId.of(documentTypeId))
                .map(mapper::toResponse)
                .orElseThrow(() -> DocumentTypeNotFoundException.byId(documentTypeId));
    }
}