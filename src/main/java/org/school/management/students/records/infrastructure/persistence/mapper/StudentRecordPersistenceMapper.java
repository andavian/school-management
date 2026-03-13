package org.school.management.students.records.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.valueobject.*;
import org.school.management.students.records.infrastructure.persistence.entity.RecordDocumentEntity;
import org.school.management.students.records.infrastructure.persistence.entity.StudentRecordEntity;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.util.List;

/**
 * PersistenceMapper para StudentRecord y RecordDocument.
 * Usa métodos default por la complejidad de los VOs anidados.
 * No requiere @AfterMapping — todos los VOs son de un solo campo.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentRecordPersistenceMapper {

    // ── RecordDocument: domain → entity ──────────────────────────────────

    default RecordDocumentEntity toDocumentEntity(RecordDocument domain) {
        if (domain == null) return null;

        RecordDocumentEntity entity = new RecordDocumentEntity();
        entity.setDocumentId(domain.getDocumentId().value());
        entity.setRecordId(domain.getRecordId().value());
        entity.setDocumentTypeId(domain.getDocumentTypeId().value());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setFilePath(domain.getFilePath());
        entity.setFileName(domain.getFileName());
        entity.setFileSize(domain.getFileSizeBytes());
        entity.setMimeType(domain.getMimeType());
        entity.setIssueDate(domain.getIssueDate());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setIssuingAuthority(domain.getIssuingAuthority());
        entity.setStatus(domain.getStatus());
        entity.setReviewObservations(domain.getReviewObservations());
        entity.setUploadedBy(domain.getUploadedBy().value());
        entity.setUploadedAt(domain.getUploadedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    // ── RecordDocument: entity → domain ──────────────────────────────────

    default RecordDocument toDocumentDomain(RecordDocumentEntity entity) {
        if (entity == null) return null;

        return RecordDocument.builder()
                .documentId(DocumentId.of(entity.getDocumentId()))
                .recordId(RecordId.of(entity.getRecordId()))
                .documentTypeId(DocumentTypeId.of(entity.getDocumentTypeId()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .filePath(entity.getFilePath())
                .fileName(entity.getFileName())
                .fileSizeBytes(entity.getFileSize())
                .mimeType(entity.getMimeType())
                .issueDate(entity.getIssueDate())
                .expiryDate(entity.getExpiryDate())
                .issuingAuthority(entity.getIssuingAuthority())
                .status(entity.getStatus())
                .reviewObservations(entity.getReviewObservations())
                .uploadedBy(UserId.from(entity.getUploadedBy()))
                .uploadedAt(entity.getUploadedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ── StudentRecord: domain → entity ────────────────────────────────────

    default StudentRecordEntity toEntity(StudentRecord domain) {
        if (domain == null) return null;

        StudentRecordEntity entity = new StudentRecordEntity();
        entity.setRecordId(domain.getRecordId().value());
        entity.setStudentId(domain.getStudentId().value());
        entity.setAcademicYearId(domain.getAcademicYearId().value());
        entity.setRecordNumber(domain.getRecordNumber().value());
        entity.setRegistryId(domain.getRegistryId().value());
        entity.setFolioNumber(domain.getFolioNumber());
        entity.setStatus(domain.getStatus());
        entity.setCompletenessPercentage(domain.getCompletenessPercentage());
        entity.setReviewedBy(
                domain.getReviewedBy() != null ? domain.getReviewedBy().value() : null
        );
        entity.setReviewedAt(domain.getReviewedAt());
        entity.setReviewObservations(domain.getReviewObservations());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    // ── StudentRecord: entity → domain ────────────────────────────────────

    default StudentRecord toDomain(StudentRecordEntity entity, List<RecordDocumentEntity> documentEntities) {
        if (entity == null) return null;

        List<RecordDocument> documents = documentEntities.stream()
                .map(this::toDocumentDomain)
                .toList();

        return StudentRecord.builder()
                .recordId(RecordId.of(entity.getRecordId()))
                .studentId(StudentPersonalDataId.of(entity.getStudentId()))
                .academicYearId(AcademicYearId.of(entity.getAcademicYearId()))
                .recordNumber(RecordNumber.of(entity.getRecordNumber()))
                .registryId(RegistryId.of(entity.getRegistryId()))
                .folioNumber(entity.getFolioNumber())
                .status(entity.getStatus())
                .completenessPercentage(entity.getCompletenessPercentage())
                .reviewedBy(
                        entity.getReviewedBy() != null
                                ? UserId.from(entity.getReviewedBy()) : null
                )
                .reviewedAt(entity.getReviewedAt())
                .reviewObservations(entity.getReviewObservations())
                .documents(new java.util.ArrayList<>(documents))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ── Listas ────────────────────────────────────────────────────────────

    default List<RecordDocumentEntity> toDocumentEntityList(List<RecordDocument> documents) {
        if (documents == null) return List.of();
        return documents.stream().map(this::toDocumentEntity).toList();
    }
}