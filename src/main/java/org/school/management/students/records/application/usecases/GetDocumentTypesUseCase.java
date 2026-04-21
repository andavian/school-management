package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetDocumentTypesUseCase {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeApplicationMapper mapper;

    /**
     * Retorna tipos de documento aplicando filtros opcionales.
     *
     * @param onlyActive   si true, retorna solo los activos
     * @param category     filtra por categoría (puede ser null)
     * @param onlyMandatory si true, retorna solo los obligatorios (requiere category)
     */
    public List<DocumentTypeResponse> execute(
            boolean onlyActive,
            DocumentCategory category,
            Boolean onlyMandatory) {

        log.debug("GetDocumentTypes — onlyActive: {}, category: {}, onlyMandatory: {}",
                onlyActive, category, onlyMandatory);

        var documentTypes = resolveQuery(onlyActive, category, onlyMandatory);

        return documentTypes.stream()
                .map(mapper::toResponse)
                .toList();
    }

    private java.util.List<org.school.management.students.records.domain.model.DocumentType> resolveQuery(
            boolean onlyActive,
            DocumentCategory category,
            Boolean onlyMandatory) {

        if (category != null) {
            if (onlyActive) {
                return documentTypeRepository.findActiveByCategoryAndMandatory(category, onlyMandatory);
            }
            return documentTypeRepository.findByCategory(category);
        }

        if (onlyActive) {
            return documentTypeRepository.findAllActive();
        }

        return documentTypeRepository.findAll();
    }
}