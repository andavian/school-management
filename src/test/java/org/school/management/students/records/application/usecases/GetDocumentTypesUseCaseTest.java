package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetDocumentTypesUseCase")
class GetDocumentTypesUseCaseTest {

    @Mock private DocumentTypeRepository      documentTypeRepository;
    @Mock private DocumentTypeApplicationMapper mapper;

    @InjectMocks private GetDocumentTypesUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private DocumentType buildDocumentType(DocumentCategory category, boolean mandatory) {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.generate())
                .name("DNI Frente")
                .code("DNI_FRONT")
                .category(category)
                .isMandatory(mandatory)
                .isActive(true)
                .build();
    }

    private DocumentTypeResponse buildResponse(DocumentType dt) {
        return new DocumentTypeResponse(
                dt.getDocumentTypeId().value(),
                dt.getName(),
                dt.getCode(),
                dt.getCategory(),
                dt.isMandatory(),
                dt.getDescription(),
                dt.getValidForYears(),
                dt.isPermanent(),
                dt.isActive()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — onlyActive=true, sin filtros — invoca findAllActive")
    void execute_onlyActive_noFilters_invokesFindAllActive() {
        DocumentType dt = buildDocumentType(DocumentCategory.PERSONAL, true);
        when(documentTypeRepository.findAllActive()).thenReturn(List.of(dt));
        when(mapper.toResponse(dt)).thenReturn(buildResponse(dt));

        List<DocumentTypeResponse> result = useCase.execute(true, null, null);

        assertThat(result).hasSize(1);
        verify(documentTypeRepository).findAllActive();
        verifyNoMoreInteractions(documentTypeRepository);
    }

    @Test
    @DisplayName("execute — onlyActive=false, sin filtros — invoca findAll")
    void execute_allDocuments_noFilters_invokesFindAll() {
        DocumentType dt = buildDocumentType(DocumentCategory.PERSONAL, false);
        when(documentTypeRepository.findAll()).thenReturn(List.of(dt));
        when(mapper.toResponse(dt)).thenReturn(buildResponse(dt));

        List<DocumentTypeResponse> result = useCase.execute(false, null, null);

        assertThat(result).hasSize(1);
        verify(documentTypeRepository).findAll();
        verifyNoMoreInteractions(documentTypeRepository);
    }

    @Test
    @DisplayName("execute — con categoría y onlyActive=true — invoca findActiveByCategoryAndMandatory")
    void execute_withCategory_onlyActive_invokesActiveByCategoryAndMandatory() {
        DocumentType dt = buildDocumentType(DocumentCategory.MEDICAL, true);
        when(documentTypeRepository.findActiveByCategoryAndMandatory(DocumentCategory.MEDICAL, null))
                .thenReturn(List.of(dt));
        when(mapper.toResponse(dt)).thenReturn(buildResponse(dt));

        List<DocumentTypeResponse> result = useCase.execute(true, DocumentCategory.MEDICAL, null);

        assertThat(result).hasSize(1);
        verify(documentTypeRepository).findActiveByCategoryAndMandatory(DocumentCategory.MEDICAL, null);
        verifyNoMoreInteractions(documentTypeRepository);
    }

    @Test
    @DisplayName("execute — con categoría y onlyActive=false — invoca findByCategory")
    void execute_withCategory_notOnlyActive_invokesFindByCategory() {
        DocumentType dt = buildDocumentType(DocumentCategory.ACADEMIC, false);
        when(documentTypeRepository.findByCategory(DocumentCategory.ACADEMIC))
                .thenReturn(List.of(dt));
        when(mapper.toResponse(dt)).thenReturn(buildResponse(dt));

        List<DocumentTypeResponse> result = useCase.execute(false, DocumentCategory.ACADEMIC, null);

        assertThat(result).hasSize(1);
        verify(documentTypeRepository).findByCategory(DocumentCategory.ACADEMIC);
        verifyNoMoreInteractions(documentTypeRepository);
    }

    @Test
    @DisplayName("execute — sin resultados — retorna lista vacía")
    void execute_noResults_returnsEmptyList() {
        when(documentTypeRepository.findAllActive()).thenReturn(List.of());

        List<DocumentTypeResponse> result = useCase.execute(true, null, null);

        assertThat(result).isEmpty();
        verify(documentTypeRepository).findAllActive();
    }
}