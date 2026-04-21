package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.request.DocumentTypeRequest.UpdateDocumentTypeRequest;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.exception.DocumentTypeNotFoundException;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateDocumentTypeUseCase")
class UpdateDocumentTypeUseCaseTest {

    @Mock private DocumentTypeRepository       documentTypeRepository;
    @Mock private DocumentTypeApplicationMapper mapper;

    @InjectMocks private UpdateDocumentTypeUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID DOC_TYPE_UUID = UUID.randomUUID();

    private DocumentType buildDocumentType() {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.of(DOC_TYPE_UUID))
                .name("DNI Frente")
                .code("DNI_FRONT")
                .category(DocumentCategory.PERSONAL)
                .isMandatory(true)
                .isActive(true)
                .build();
    }

    private UpdateDocumentTypeRequest buildUpdateRequest() {
        return new UpdateDocumentTypeRequest(
                "DNI Frente Actualizado",
                DocumentCategory.PERSONAL,
                false,
                "Descripción actualizada",
                null
        );
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
    @DisplayName("execute — tipo existe — actualiza y retorna DocumentTypeResponse")
    void execute_whenDocumentTypeExists_thenUpdateAndReturnResponse() {
        DocumentType dt = buildDocumentType();
        UpdateDocumentTypeRequest request = buildUpdateRequest();

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.of(dt));
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(dt);
        when(mapper.toResponse(dt)).thenReturn(buildResponse(dt));

        DocumentTypeResponse result = useCase.execute(DOC_TYPE_UUID, request);

        assertThat(result).isNotNull();
        assertThat(result.documentTypeId()).isEqualTo(DOC_TYPE_UUID);
        verify(documentTypeRepository).findById(DocumentTypeId.of(DOC_TYPE_UUID));
        verify(documentTypeRepository).save(dt);
    }

    @Test
    @DisplayName("execute — tipo no existe — lanza DocumentTypeNotFoundException")
    void execute_whenDocumentTypeNotFound_thenThrowDocumentTypeNotFoundException() {
        UpdateDocumentTypeRequest request = buildUpdateRequest();

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(DOC_TYPE_UUID, request))
                .isInstanceOf(DocumentTypeNotFoundException.class)
                .hasMessageContaining(DOC_TYPE_UUID.toString());

        verify(documentTypeRepository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("execute — update aplica cambios al modelo de dominio antes de guardar")
    void execute_callsUpdateOnDomainModel_beforeSaving() {
        DocumentType dt = buildDocumentType();
        UpdateDocumentTypeRequest request = new UpdateDocumentTypeRequest(
                "Nombre Nuevo",
                DocumentCategory.LEGAL,
                false,
                "Nueva descripción",
                3
        );

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.of(dt));
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(dt);
        when(mapper.toResponse(any())).thenReturn(buildResponse(dt));

        useCase.execute(DOC_TYPE_UUID, request);

        // Verifica que el dominio fue mutado correctamente antes del save
        verify(documentTypeRepository).save(argThat(saved ->
                saved.getName().equals("Nombre Nuevo") &&
                        saved.getCategory() == DocumentCategory.LEGAL &&
                        !saved.isMandatory() &&
                        saved.getValidForYears() == 3
        ));
    }
}