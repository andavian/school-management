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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ToggleDocumentTypeStatusUseCase")
class ToggleDocumentTypeStatusUseCaseTest {

    @Mock private DocumentTypeRepository       documentTypeRepository;
    @Mock private DocumentTypeApplicationMapper mapper;

    @InjectMocks private ToggleDocumentTypeStatusUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID DOC_TYPE_UUID = UUID.randomUUID();

    private DocumentType buildActiveDocumentType() {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.of(DOC_TYPE_UUID))
                .name("Carnet de Vacunación")
                .code("VACCINATION_CARD")
                .category(DocumentCategory.MEDICAL)
                .isMandatory(false)
                .isActive(true)
                .build();
    }

    private DocumentType buildInactiveDocumentType() {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.of(DOC_TYPE_UUID))
                .name("Carnet de Vacunación")
                .code("VACCINATION_CARD")
                .category(DocumentCategory.MEDICAL)
                .isMandatory(false)
                .isActive(false)
                .build();
    }

    private DocumentTypeResponse buildResponse(boolean active) {
        return new DocumentTypeResponse(
                DOC_TYPE_UUID, "Carnet de Vacunación", "VACCINATION_CARD",
                DocumentCategory.MEDICAL, false, null, null, true, active
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — activate=true sobre tipo activo — persiste y retorna activo")
    void execute_activate_onActiveType_persistsAndReturnsActive() {
        DocumentType dt = buildActiveDocumentType();

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.of(dt));
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(dt);
        when(mapper.toResponse(dt)).thenReturn(buildResponse(true));

        DocumentTypeResponse result = useCase.execute(DOC_TYPE_UUID, true);

        assertThat(result.active()).isTrue();
        verify(documentTypeRepository).save(argThat(DocumentType::isActive));
    }

    @Test
    @DisplayName("execute — activate=false sobre tipo activo — desactiva y persiste")
    void execute_deactivate_onActiveType_deactivatesAndPersists() {
        DocumentType dt = buildActiveDocumentType();

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.of(dt));
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(dt);
        when(mapper.toResponse(dt)).thenReturn(buildResponse(false));

        DocumentTypeResponse result = useCase.execute(DOC_TYPE_UUID, false);

        assertThat(result.active()).isFalse();
        verify(documentTypeRepository).save(argThat(saved -> !saved.isActive()));
    }

    @Test
    @DisplayName("execute — activate=true sobre tipo inactivo — reactiva y persiste")
    void execute_activate_onInactiveType_reactivatesAndPersists() {
        DocumentType dt = buildInactiveDocumentType();

        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.of(dt));
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(dt);
        when(mapper.toResponse(dt)).thenReturn(buildResponse(true));

        DocumentTypeResponse result = useCase.execute(DOC_TYPE_UUID, true);

        assertThat(result.active()).isTrue();
        verify(documentTypeRepository).save(argThat(DocumentType::isActive));
    }

    @Test
    @DisplayName("execute — tipo no existe — lanza DocumentTypeNotFoundException")
    void execute_whenDocumentTypeNotFound_thenThrowDocumentTypeNotFoundException() {
        when(documentTypeRepository.findById(DocumentTypeId.of(DOC_TYPE_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(DOC_TYPE_UUID, true))
                .isInstanceOf(DocumentTypeNotFoundException.class)
                .hasMessageContaining(DOC_TYPE_UUID.toString());

        verify(documentTypeRepository, never()).save(any());
        verifyNoInteractions(mapper);
    }
}