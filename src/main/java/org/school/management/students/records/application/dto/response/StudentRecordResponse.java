package org.school.management.students.records.application.dto.response;

import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.domain.valueobject.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO para StudentRecord.
 * Incluye el legajo completo con todos sus documentos.
 */
public record StudentRecordResponse(

        UUID recordId,
        UUID studentId,
        UUID academicYearId,

        // Identificación del legajo
        String recordNumber,      // DNI del estudiante
        UUID registryId,
        Integer folioNumber,

        // Estado
        RecordStatus status,
        BigDecimal completenessPercentage,

        // Revisión administrativa
        UUID reviewedBy,
        LocalDateTime reviewedAt,
        String reviewObservations,

        // Documentos
        List<RecordDocumentResponse> documents,
        int totalDocuments,

        // Flags calculados del dominio
        boolean complete,
        boolean hasExpiredDocuments,
        boolean hasExpiringSoonDocuments,
        Map<DocumentStatus, Long> documentCountByStatus,

        // Auditoría
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}