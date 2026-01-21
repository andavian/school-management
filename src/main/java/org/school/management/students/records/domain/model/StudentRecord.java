package org.school.management.students.records.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId; // ✅ CORREGIDO
import org.school.management.students.records.domain.exception.*;
import org.school.management.students.records.domain.valueobject.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Agregado Root: Legajo del Estudiante
 *
 * Responsabilidad: Gestionar el legajo digital anual del estudiante
 * y todos sus documentos asociados
 *
 * Reglas de negocio críticas:
 * - Un estudiante tiene UN legajo por año académico
 * - El folio se asigna automáticamente del registro de calificaciones
 * - El legajo solo se puede aprobar si tiene todos los docs obligatorios
 * - Los documentos solo se pueden subir/modificar si el legajo NO está aprobado
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class StudentRecord {

    // Identidad
    @EqualsAndHashCode.Include
    private final RecordId recordId;
    private final StudentPersonalDataId studentId; // ✅ CORREGIDO
    private final AcademicYearId academicYearId;

    // Número de legajo y folio
    private final RecordNumber recordNumber;
    private final RegistryId registryId;
    private final Integer folioNumber;

    // Estado del legajo
    @Builder.Default
    private RecordStatus status = RecordStatus.INCOMPLETE;
    @Builder.Default
    private BigDecimal completenessPercentage = BigDecimal.ZERO;

    // Revisión administrativa
    private UserId reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewObservations;

    // Documentos (colección interna protegida)
    private final List<RecordDocument> documents; // ✅ SIN @Singular

    // Auditoría
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();


    // ============ Domain Logic - Gestión de Documentos ============

    /**
     * Agrega un documento al legajo
     */
    public void addDocument(RecordDocument document) {
        Objects.requireNonNull(document, "Document cannot be null");

        if (status == RecordStatus.APPROVED) {
            throw new RecordAlreadyApprovedException(
                    "Cannot add documents to an approved record: " + recordId
            );
        }

        if (!document.getRecordId().equals(recordId)) {
            throw new IllegalArgumentException(
                    "Document does not belong to this record"
            );
        }

        this.documents.add(document);
        recalculateCompleteness();
        updateStatusBasedOnDocuments();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Remueve un documento del legajo
     */
    public void removeDocument(DocumentId documentId) {
        Objects.requireNonNull(documentId, "DocumentId cannot be null");

        if (status == RecordStatus.APPROVED) {
            throw new RecordAlreadyApprovedException(
                    "Cannot remove documents from an approved record: " + recordId
            );
        }

        boolean removed = documents.removeIf(doc -> doc.getDocumentId().equals(documentId));

        if (!removed) {
            throw new DocumentNotFoundException(
                    "Document not found in record: " + documentId
            );
        }

        recalculateCompleteness();
        updateStatusBasedOnDocuments();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Obtiene un documento por su ID
     */
    public Optional<RecordDocument> getDocument(DocumentId documentId) {
        return documents.stream()
                .filter(doc -> doc.getDocumentId().equals(documentId))
                .findFirst();
    }

    /**
     * Obtiene todos los documentos del legajo (inmutable)
     */
    public List<RecordDocument> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    /**
     * Obtiene documentos por tipo
     */
    public List<RecordDocument> getDocumentsByType(DocumentTypeId typeId) {
        return documents.stream()
                .filter(doc -> doc.getDocumentTypeId().equals(typeId))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene documentos por estado
     */
    public List<RecordDocument> getDocumentsByStatus(DocumentStatus docStatus) {
        return documents.stream()
                .filter(doc -> doc.getStatus() == docStatus)
                .collect(Collectors.toList());
    }

    // ============ Domain Logic - Workflow de Aprobación ============

    /**
     * Marca el legajo como pendiente de revisión
     */
    public void submitForReview(Set<DocumentTypeId> mandatoryTypes) {
        Objects.requireNonNull(mandatoryTypes, "Mandatory types cannot be null");

        if (status == RecordStatus.APPROVED) {
            throw new RecordAlreadyApprovedException(
                    "Record is already approved: " + recordId
            );
        }

        Set<DocumentTypeId> uploadedTypes = documents.stream()
                .map(RecordDocument::getDocumentTypeId)
                .collect(Collectors.toSet());

        Set<DocumentTypeId> missingTypes = new HashSet<>(mandatoryTypes);
        missingTypes.removeAll(uploadedTypes);

        if (!missingTypes.isEmpty()) {
            throw new IncompleteRecordException(
                    "Record is missing mandatory documents: " + missingTypes.size()
            );
        }

        this.status = RecordStatus.PENDING_REVIEW;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Aprueba el legajo tras revisión administrativa
     */
    public void approve(UserId reviewedBy, String observations) {
        Objects.requireNonNull(reviewedBy, "ReviewedBy cannot be null");

        if (status == RecordStatus.APPROVED) {
            throw new RecordAlreadyApprovedException(
                    "Record is already approved: " + recordId
            );
        }

        long pendingCount = documents.stream()
                .filter(doc -> doc.getStatus() != DocumentStatus.APPROVED)
                .count();

        if (pendingCount > 0) {
            throw new RecordNotReadyForApprovalException(
                    "Record has " + pendingCount + " documents not approved"
            );
        }

        this.status = RecordStatus.APPROVED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.reviewObservations = observations;
        this.completenessPercentage = BigDecimal.valueOf(100.0);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rechaza el legajo con motivo
     */
    public void reject(UserId reviewedBy, String reason) {
        Objects.requireNonNull(reviewedBy, "ReviewedBy cannot be null");

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason cannot be null or empty");
        }

        if (status == RecordStatus.APPROVED) {
            throw new RecordAlreadyApprovedException(
                    "Cannot reject an approved record: " + recordId
            );
        }

        this.status = RecordStatus.REJECTED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.reviewObservations = reason;
        this.updatedAt = LocalDateTime.now();
    }

    // ============ Domain Logic - Cálculos ============

    private void recalculateCompleteness() {
        if (documents.isEmpty()) {
            this.completenessPercentage = BigDecimal.ZERO;
            return;
        }

        long approvedCount = documents.stream()
                .filter(RecordDocument::isApproved)
                .count();

        BigDecimal percentage = BigDecimal.valueOf(approvedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(documents.size()), 2, RoundingMode.HALF_UP);

        this.completenessPercentage = percentage;
    }

    private void updateStatusBasedOnDocuments() {
        if (status == RecordStatus.APPROVED) {
            return;
        }

        boolean hasRejected = documents.stream()
                .anyMatch(RecordDocument::isRejected);

        if (hasRejected) {
            this.status = RecordStatus.REJECTED;
        } else if (documents.isEmpty()) {
            this.status = RecordStatus.INCOMPLETE;
        }
    }

    public boolean isComplete() {
        return completenessPercentage.compareTo(BigDecimal.valueOf(100.0)) == 0;
    }

    public boolean hasExpiredDocuments() {
        return documents.stream().anyMatch(RecordDocument::isExpired);
    }

    public boolean hasExpiringSoonDocuments() {
        return documents.stream().anyMatch(RecordDocument::isExpiringSoon);
    }

    public List<RecordDocument> getExpiredDocuments() {
        return documents.stream()
                .filter(RecordDocument::isExpired)
                .collect(Collectors.toList());
    }

    public Map<DocumentStatus, Long> countDocumentsByStatus() {
        return documents.stream()
                .collect(Collectors.groupingBy(
                        RecordDocument::getStatus,
                        Collectors.counting()
                ));
    }

    // Factory method para encapsular lógica de creación
    public static StudentRecord create(StudentRecordBuilder builder) {
        Objects.requireNonNull(builder.recordId, "RecordId cannot be null");
        Objects.requireNonNull(builder.studentId, "StudentId cannot be null");
        Objects.requireNonNull(builder.academicYearId, "AcademicYearId cannot be null");
        Objects.requireNonNull(builder.recordNumber, "RecordNumber cannot be null");
        Objects.requireNonNull(builder.registryId, "RegistryId cannot be null");
        Objects.requireNonNull(builder.folioNumber, "FolioNumber cannot be null");

        if (builder.folioNumber <= 0) {
            throw new IllegalArgumentException("Folio number must be positive");
        }

        return builder.build();
    }
}