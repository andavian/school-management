package org.school.management.students.records.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.school.management.students.records.application.dto.request.AddDocumentRequest;
import org.school.management.students.records.application.dto.request.UpdateRecordStatusRequest;
import org.school.management.students.records.application.dto.response.RecordDocumentResponse;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.infrastructure.web.dto.RecordWebDto;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper de capa Web: web DTOs ↔ application DTOs.
 * Tercera capa de mappers — nunca saltear a domain directamente.
 */
@Mapper(componentModel = "spring")
public interface RecordWebMapper {

    // ── web request → application request ────────────────────────────────

    AddDocumentRequest toApplicationRequest(
            RecordWebDto.AddDocumentWebRequest webRequest
    );

    UpdateRecordStatusRequest toApplicationRequest(
            RecordWebDto.UpdateRecordStatusWebRequest webRequest
    );

    // ── application response → web response ──────────────────────────────

    default RecordWebDto.RecordDocumentWebResponse toDocumentWebResponse(
            RecordDocumentResponse response) {
        if (response == null) return null;

        return new RecordWebDto.RecordDocumentWebResponse(
                response.documentId(),
                response.documentTypeId(),
                response.title(),
                response.description(),
                response.fileName(),
                response.mimeType(),
                response.fileSizeBytes(),
                response.fileSizeMB(),
                response.issueDate(),
                response.expiryDate(),
                response.issuingAuthority(),
                response.status().name(),
                response.reviewObservations(),
                response.approved(),
                response.rejected(),
                response.pending(),
                response.valid(),
                response.expired(),
                response.expiringSoon(),
                response.uploadedAt(),
                response.updatedAt()
        );
    }

    default RecordWebDto.StudentRecordWebResponse toWebResponse(
            StudentRecordResponse response) {
        if (response == null) return null;

        // Convertir Map<DocumentStatus, Long> → Map<String, Long> para la API
        Map<String, Long> documentCountByStatus = response.documentCountByStatus()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Map.Entry::getValue
                ));

        return new RecordWebDto.StudentRecordWebResponse(
                response.recordId(),
                response.studentId(),
                response.recordNumber(),
                response.registryId(),
                response.folioNumber(),
                response.status().name(),
                response.completenessPercentage(),
                response.reviewedBy(),
                response.reviewedAt(),
                response.reviewObservations(),
                response.documents().stream()
                        .map(this::toDocumentWebResponse)
                        .toList(),
                response.totalDocuments(),
                response.complete(),
                response.hasExpiredDocuments(),
                response.hasExpiringSoonDocuments(),
                documentCountByStatus,
                response.createdAt(),
                response.updatedAt()
        );
    }
}