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
import org.school.management.students.records.domain.exception.DocumentTypeNotFoundException;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetDocumentTypeByIdUseCase")
class GetDocumentTypeByIdUseCaseTest {

    @Mock private DocumentTypeRepository       documentTypeRepository;
    @Mock private DocumentTypeApplicationMapper mapper;

    @InjectMocks private GetDocumentTypeByIdUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID DOC_TYPE_UUID = UUID.randomUUID();

    private DocumentType buildDocumentType() {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.of(DOC_TYPE_UUID))
                .name("Acta de Nacimiento")
                .code("BIRTH_CERTIFICATE")
                .category(DocumentCategory.PERSONAL)
                .isMandatory(true)
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
    @DisplayName("execute — cuando el tipo existe — retorna DocumentTypeResponse")
    void execute_whenDocumentTypeExists_thenReturnResponse() {
        DocumentType dt = buildDocumentType();
        DocumentTypeResponse expected = buildResponse(dt);

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.of(dt));
        when(mapper.toResponse(dt)).thenReturn(expected);

        DocumentTypeResponse result = useCase.execute(DOC_TYPE_UUID);

        assertThat(result).isNotNull();
        assertThat(result.documentTypeId()).isEqualTo(DOC_TYPE_UUID);
        assertThat(result.code()).isEqualTo("BIRTH_CERTIFICATE");
        assertThat(result.category()).isEqualTo(DocumentCategory.PERSONAL);
        verify(documentTypeRepository).findById(DocumentTypeId.of(DOC_TYPE_UUID));
    }

    @Test
    @DisplayName("execute — cuando el tipo no existe — lanza DocumentTypeNotFoundException")
    void execute_whenDocumentTypeNotFound_thenThrowDocumentTypeNotFoundException() {
        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(DOC_TYPE_UUID))
                .isInstanceOf(DocumentTypeNotFoundException.class)
                .hasMessageContaining(DOC_TYPE_UUID.toString());

        verify(documentTypeRepository).findById(DocumentTypeId.of(DOC_TYPE_UUID));
        verifyNoInteractions(mapper);
    }
}