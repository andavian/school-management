package org.school.management.students.records.infrastructure.web.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Clase contenedora de todos los Web DTOs del módulo records.
 * Patrón consistente con StudentWebDto, HealthRecordWebDto y EnrollmentWebDto.
 */
public final class RecordWebDto {

    private RecordWebDto() {}

    // ── Requests ──────────────────────────────────────────────────────────

    public record AddDocumentWebRequest(

            @NotNull(message = "El tipo de documento es obligatorio")
            UUID documentTypeId,

            @NotBlank(message = "El título es obligatorio")
            @Size(max = 200, message = "El título no puede superar los 200 caracteres")
            String title,

            @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
            String description,

            @NotBlank(message = "La ruta del archivo es obligatoria")
            @Size(max = 500)
            String filePath,

            @NotBlank(message = "El nombre del archivo es obligatorio")
            @Size(max = 255)
            String fileName,

            @NotNull(message = "El tamaño del archivo es obligatorio")
            @Min(value = 1, message = "El tamaño del archivo debe ser mayor a 0")
            Long fileSizeBytes,

            @NotBlank(message = "El tipo MIME es obligatorio")
            @Size(max = 100)
            String mimeType,

            @PastOrPresent(message = "La fecha de emisión no puede ser futura")
            LocalDate issueDate,

            LocalDate expiryDate,

            @Size(max = 200)
            String issuingAuthority

    ) {}

    public record UpdateRecordStatusWebRequest(

            // Revisión de documento individual
            UUID documentId,

            @Pattern(regexp = "APPROVE|REJECT",
                    message = "La acción sobre el documento debe ser APPROVE o REJECT")
            String documentAction,

            @Size(max = 500)
            String documentObservations,

            // Workflow del legajo completo
            @Pattern(regexp = "SUBMIT|APPROVE|REJECT",
                    message = "La acción del legajo debe ser SUBMIT, APPROVE o REJECT")
            String recordAction,

            @Size(max = 500)
            String recordObservations,

            Set<UUID> mandatoryDocumentTypeIds

    ) {}

    // ── Responses ─────────────────────────────────────────────────────────

    public record RecordDocumentWebResponse(
            UUID documentId,
            UUID documentTypeId,
            String title,
            String description,
            String fileName,
            String mimeType,
            Long fileSizeBytes,
            double fileSizeMB,
            LocalDate issueDate,
            LocalDate expiryDate,
            String issuingAuthority,
            String status,
            String reviewObservations,
            boolean approved,
            boolean rejected,
            boolean pending,
            boolean valid,
            boolean expired,
            boolean expiringSoon,
            LocalDateTime uploadedAt,
            LocalDateTime updatedAt
    ) {}

    public record StudentRecordWebResponse(
            UUID recordId,
            UUID studentId,
            String recordNumber,       // DNI del estudiante
            UUID registryId,
            Integer folioNumber,
            String status,
            BigDecimal completenessPercentage,
            UUID reviewedBy,
            LocalDateTime reviewedAt,
            String reviewObservations,
            List<RecordDocumentWebResponse> documents,
            int totalDocuments,
            boolean complete,
            boolean hasExpiredDocuments,
            boolean hasExpiringSoonDocuments,
            Map<String, Long> documentCountByStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}