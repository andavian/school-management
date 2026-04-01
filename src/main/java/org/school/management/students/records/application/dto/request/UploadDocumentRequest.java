package org.school.management.students.records.application.dto.request;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de aplicación para subir un documento al legajo.
 * El archivo llega por separado como {@code MultipartFile}.
 */
public record UploadDocumentRequest(
        UUID documentTypeId,
        String title,
        String description,
        LocalDate issueDate,
        LocalDate expiryDate,
        String issuingAuthority
) {}