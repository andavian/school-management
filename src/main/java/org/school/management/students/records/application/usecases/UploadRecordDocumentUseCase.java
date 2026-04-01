package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.storage.domain.model.UploadedFile;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.students.records.application.dto.request.UploadDocumentRequest;
import org.school.management.students.records.application.dto.response.RecordDocumentResponse;
import org.school.management.students.records.application.mapper.StudentRecordApplicationMapper;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.RecordDocumentRepository;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.records.domain.valueobject.DocumentId;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Caso de uso: subir un documento al legajo del estudiante.
 *
 * <p>Flujo:</p>
 * <ol>
 *   <li>Valida tipo MIME y tamaño del archivo</li>
 *   <li>Busca el {@link StudentRecord} por su ID</li>
 *   <li>Sube el archivo a OCI Object Storage via {@link StorageService}</li>
 *   <li>Construye el {@link RecordDocument} con la URL resultante</li>
 *   <li>Agrega el documento al agregado {@link StudentRecord}</li>
 *   <li>Persiste documento y legajo en la misma transacción</li>
 * </ol>
 *
 * <p>Si la subida a OCI falla, la transacción no llega a persistir nada
 * — no quedan registros huérfanos en BD.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UploadRecordDocumentUseCase {

    private static final int  MAX_SIZE_MB    = 10;
    private static final long MAX_SIZE_BYTES = (long) MAX_SIZE_MB * 1024 * 1024;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final StudentRecordRepository      recordRepository;
    private final RecordDocumentRepository     documentRepository;
    private final StorageService               storageService;
    private final StudentRecordApplicationMapper mapper;

    public RecordDocumentResponse execute(UUID recordId,
                                          UploadDocumentRequest request,
                                          MultipartFile file,
                                          UUID uploadedByUserId) {
        // ── 1. Validar archivo ────────────────────────────────────────────
        validateFile(file);

        // ── 2. Buscar legajo ──────────────────────────────────────────────
        StudentRecord record = recordRepository
                .findByRecordId(RecordId.of(recordId))
                .orElseThrow(() -> new RecordNotFoundException(
                        "StudentRecord not found: " + recordId));

        // ── 3. Subir a OCI ────────────────────────────────────────────────
        // Carpeta: records/{studentId}/
        String folder = "records/" + record.getStudentId().value();

        UploadedFile uploaded;
        try {
            uploaded = storageService.upload(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    folder
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error leyendo el archivo para subir: " + e.getMessage(), e);
        }

        log.info("Documento subido a OCI — recordId: {}, object: {}",
                recordId, uploaded.objectName());

        // ── 4. Construir RecordDocument ───────────────────────────────────
        // filePath guarda el objectName (para delete y presigned URLs)
        // fileName guarda la URL pública (para acceso directo)
        RecordDocument document = RecordDocument.builder()
                .documentId(DocumentId.generate())
                .recordId(RecordId.of(recordId))
                .documentTypeId(DocumentTypeId.of(request.documentTypeId()))
                .title(request.title())
                .description(request.description())
                .filePath(uploaded.objectName())
                .fileName(uploaded.publicUrl())
                .fileSizeBytes(uploaded.sizeBytes())
                .mimeType(uploaded.mimeType())
                .issueDate(request.issueDate())
                .expiryDate(request.expiryDate())
                .issuingAuthority(request.issuingAuthority())
                .uploadedBy(UserId.from(uploadedByUserId))
                .build();

        // ── 5. Agregar al agregado y persistir ────────────────────────────
        record.addDocument(document);
        documentRepository.save(document);
        recordRepository.save(record);

        log.info("RecordDocument persistido — documentId: {}, recordId: {}",
                document.getDocumentId().value(), recordId);

        return mapper.toDocumentResponse(document);
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Tipo de archivo no permitido: " + file.getContentType()
                            + ". Solo se aceptan PDF, JPG y PNG.");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "El archivo supera el tamaño máximo de " + MAX_SIZE_MB + " MB");
        }
    }
}