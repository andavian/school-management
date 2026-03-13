package org.school.management.students.records.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.students.records.domain.valueobject.DocumentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "record_documents")
public class RecordDocumentEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "document_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID documentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "record_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID recordId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "document_type_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID documentTypeId;

    // Información del documento
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Archivo
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    // Metadatos
    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "issuing_authority", length = 200)
    private String issuingAuthority;

    // Estado y revisión
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DocumentStatus status;

    @Column(name = "review_observations", columnDefinition = "TEXT")
    private String reviewObservations;

    // Auditoría
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "uploaded_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uploadedBy;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (uploadedAt == null) uploadedAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}