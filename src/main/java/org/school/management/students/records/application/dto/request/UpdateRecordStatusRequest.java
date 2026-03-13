package org.school.management.students.records.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;
import java.util.UUID;

/**
 * Request para operaciones de workflow del legajo:
 *  - Aprobar documento individual
 *  - Rechazar documento individual
 *  - Enviar legajo a revisión
 *  - Aprobar legajo completo
 *  - Rechazar legajo completo
 *
 * Campos null conservan valor actual (semántica PATCH).
 */
public record UpdateRecordStatusRequest(

        // ── Revisión de documento individual ─────────────────────────────
        UUID documentId,

        @Pattern(regexp = "APPROVE|REJECT",
                message = "La acción sobre el documento debe ser APPROVE o REJECT")
        String documentAction,

        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String documentObservations,

        // ── Workflow del legajo completo ──────────────────────────────────
        @Pattern(regexp = "SUBMIT|APPROVE|REJECT",
                message = "La acción del legajo debe ser SUBMIT, APPROVE o REJECT")
        String recordAction,

        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String recordObservations,

        // ── Tipos obligatorios para SUBMIT ────────────────────────────────
        Set<UUID> mandatoryDocumentTypeIds
) {}