package org.school.management.students.records.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.records.domain.exception.DocumentAlreadyApprovedException;
import org.school.management.students.records.domain.valueobject.DocumentId;
import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;
import org.school.management.students.records.domain.valueobject.RecordId;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity: Documento del Legajo

 * Representa un documento individual subido al legajo del estudiante
 * Cada documento tiene su propio ciclo de revisión y aprobación

 * NO es un agregado root, pertenece al agregado StudentRecord
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RecordDocument {

    // Identidad
    @EqualsAndHashCode.Include
    private final DocumentId documentId;
    private final RecordId recordId;
    private final DocumentTypeId documentTypeId;

    // Información del documento
    private String title;
    private String description;

    // Archivo físico
    private String filePath;
    private String fileName;
    private Long fileSizeBytes;
    private String mimeType;

    // Metadatos del documento
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String issuingAuthority;

    // Auditoría de subida
    private UserId uploadedBy;
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // Revisión
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PENDING;
    private String reviewObservations;

    // Auditoría
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ============ Domain Logic ============

    /**
     * Aprueba el documento tras revisión
     */
    public void approve(String observations) {
        if (status == DocumentStatus.APPROVED) {
            throw new DocumentAlreadyApprovedException(
                    "Document is already approved: " + documentId
            );
        }

        this.status = DocumentStatus.APPROVED;
        this.reviewObservations = observations;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rechaza el documento con motivo
     */
    public void reject(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason cannot be null or empty");
        }

        this.status = DocumentStatus.REJECTED;
        this.reviewObservations = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza metadatos del documento
     */
    public void updateMetadata(
            String title,
            String description,
            LocalDate issueDate,
            LocalDate expiryDate,
            String issuingAuthority) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (issueDate != null && issueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Issue date cannot be in the future");
        }

        if (issueDate != null && expiryDate != null && expiryDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Expiry date cannot be before issue date");
        }

        this.title = title;
        this.description = description;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issuingAuthority = issuingAuthority;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        if (expiryDate == null) {
            return true;
        }
        return !expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        LocalDate warningDate = LocalDate.now().plusDays(30);
        return expiryDate.isAfter(LocalDate.now()) && expiryDate.isBefore(warningDate);
    }

    public boolean isExpired() {
        return !isValid();
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }

    public double getFileSizeMB() {
        return fileSizeBytes / (1024.0 * 1024.0);
    }

    public boolean isApproved() {
        return status == DocumentStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == DocumentStatus.REJECTED;
    }

    public boolean isPending() {
        return status == DocumentStatus.PENDING;
    }


}