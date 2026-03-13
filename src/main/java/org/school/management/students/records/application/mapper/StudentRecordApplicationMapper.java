package org.school.management.students.records.application.mapper;

import org.mapstruct.Mapper;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.records.application.dto.response.RecordDocumentResponse;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.model.StudentRecord;

/**
 * Mapper de capa Application: domain → response DTO.
 * Usa métodos default por la complejidad de los VOs y flags calculados.
 */
@Mapper(componentModel = "spring")
public interface StudentRecordApplicationMapper {

    default RecordDocumentResponse toDocumentResponse(RecordDocument document) {
        if (document == null) return null;

        return new RecordDocumentResponse(
                document.getDocumentId().value(),
                document.getRecordId().value(),
                document.getDocumentTypeId().value(),
                document.getTitle(),
                document.getDescription(),
                document.getFileName(),
                document.getMimeType(),
                document.getFileSizeBytes(),
                document.getFileSizeMB(),
                document.getIssueDate(),
                document.getExpiryDate(),
                document.getIssuingAuthority(),
                document.getStatus(),
                document.getReviewObservations(),
                document.isApproved(),
                document.isRejected(),
                document.isPending(),
                document.isValid(),
                document.isExpired(),
                document.isExpiringSoon(),
                document.isImage(),
                document.isPdf(),
                document.getUploadedBy().value(),
                document.getUploadedAt(),
                document.getUpdatedAt()
        );
    }

    default StudentRecordResponse toRecordResponse(StudentRecord record) {
        if (record == null) return null;

        return new StudentRecordResponse(
                record.getRecordId().value(),
                record.getStudentId().value(),
                record.getAcademicYearId().value(),
                record.getRecordNumber().value(),
                record.getRegistryId().value(),
                record.getFolioNumber(),
                record.getStatus(),
                record.getCompletenessPercentage(),
                record.getReviewedBy() != null ? record.getReviewedBy().value() : null,
                record.getReviewedAt(),
                record.getReviewObservations(),
                record.getDocuments().stream()
                        .map(this::toDocumentResponse)
                        .toList(),
                record.getDocuments().size(),
                record.isComplete(),
                record.hasExpiredDocuments(),
                record.hasExpiringSoonDocuments(),
                record.countDocumentsByStatus(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}