package org.school.management.students.records.application.dto.response;

import org.school.management.students.records.domain.valueobject.DocumentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO para RecordDocument.
 * Representa un documento individual dentro del legajo.
 */
public record RecordDocumentResponse(

        UUID documentId,
        UUID recordId,
        UUID documentTypeId,

        // Información del documento
        String title,
        String description,

        // Archivo
        String fileName,
        String mimeType,
        Long fileSizeBytes,
        double fileSizeMB,

        // Metadatos
        LocalDate issueDate,
        LocalDate expiryDate,
        String issuingAuthority,

        // Estado de revisión
        DocumentStatus status,
        String reviewObservations,

        // Flags calculados del dominio
        boolean approved,
        boolean rejected,
        boolean pending,
        boolean valid,
        boolean expired,
        boolean expiringSoon,
        boolean isImage,
        boolean isPdf,

        // Auditoría
        UUID uploadedBy,
        LocalDateTime uploadedAt,
        LocalDateTime updatedAt
) {}