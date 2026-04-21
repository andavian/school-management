package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.request.DocumentTypeRequest.CreateDocumentTypeRequest;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.application.mapper.DocumentTypeApplicationMapper;
import org.school.management.students.records.domain.exception.DocumentTypeAlreadyExistsException;
import org.school.management.students.records.domain.model.DocumentType;
import org.school.management.students.records.domain.repository.DocumentTypeRepository;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateDocumentTypeUseCase")
class CreateDocumentTypeUseCaseTest {

    @Mock private DocumentTypeRepository       documentTypeRepository;
    @Mock private DocumentTypeApplicationMapper mapper;

    @InjectMocks private CreateDocumentTypeUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID DOC_TYPE_UUID = UUID.randomUUID();

    private CreateDocumentTypeRequest buildRequest() {
        return new CreateDocumentTypeRequest(
                "Certificado de Salud",
                "HEALTH_CERTIFICATE",
                DocumentCategory.MEDICAL,
                true,
                "Certificado de aptitud física emitido por médico habilitado",
                1
        );
    }

    private DocumentType buildSavedDocumentType(CreateDocumentTypeRequest req) {
        return DocumentType.builder()
                .documentTypeId(DocumentTypeId.of(DOC_TYPE_UUID))
                .name(req.name())
                .code(req.code())
                .category(req.category())
                .isMandatory(req.mandatory())
                .description(req.description())
                .validForYears(req.validForYears())
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
    @DisplayName("execute — flujo feliz — crea y retorna DocumentTypeResponse")
    void execute_happyPath_createsAndReturnsDocumentTypeResponse() {
        CreateDocumentTypeRequest request = buildRequest();
        DocumentType saved = buildSavedDocumentType(request);
        DocumentTypeResponse expected = buildResponse(saved);

        when(documentTypeRepository.existsByCode(request.code())).thenReturn(false);
        when(documentTypeRepository.existsByName(request.name())).thenReturn(false);
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(expected);

        DocumentTypeResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("HEALTH_CERTIFICATE");
        assertThat(result.category()).isEqualTo(DocumentCategory.MEDICAL);
        assertThat(result.mandatory()).isTrue();
        assertThat(result.active()).isTrue();

        verify(documentTypeRepository).existsByCode(request.code());
        verify(documentTypeRepository).existsByName(request.name());
        verify(documentTypeRepository).save(any(DocumentType.class));
    }

    @Test
    @DisplayName("execute — código duplicado — lanza DocumentTypeAlreadyExistsException")
    void execute_whenCodeExists_thenThrowDocumentTypeAlreadyExistsException() {
        CreateDocumentTypeRequest request = buildRequest();

        when(documentTypeRepository.existsByCode(request.code())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DocumentTypeAlreadyExistsException.class)
                .hasMessageContaining(request.code());

        verify(documentTypeRepository, never()).save(any());
        verify(documentTypeRepository, never()).existsByName(any());
    }

    @Test
    @DisplayName("execute — nombre duplicado — lanza DocumentTypeAlreadyExistsException")
    void execute_whenNameExists_thenThrowDocumentTypeAlreadyExistsException() {
        CreateDocumentTypeRequest request = buildRequest();

        when(documentTypeRepository.existsByCode(request.code())).thenReturn(false);
        when(documentTypeRepository.existsByName(request.name())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DocumentTypeAlreadyExistsException.class)
                .hasMessageContaining(request.name());

        verify(documentTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — tipo guardado siempre arranca activo")
    void execute_savedDocumentType_isAlwaysActive() {
        CreateDocumentTypeRequest request = buildRequest();
        DocumentType saved = buildSavedDocumentType(request);

        when(documentTypeRepository.existsByCode(request.code())).thenReturn(false);
        when(documentTypeRepository.existsByName(request.name())).thenReturn(false);
        when(documentTypeRepository.save(any(DocumentType.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(buildResponse(saved));

        useCase.execute(request);

        // Verifica que el DocumentType enviado al save tenga isActive = true
        verify(documentTypeRepository).save(argThat(DocumentType::isActive));
    }
}