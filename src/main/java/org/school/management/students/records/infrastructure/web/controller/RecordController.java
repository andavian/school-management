package org.school.management.students.records.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.infra.security.UserPrincipal;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.students.records.application.dto.request.UploadDocumentRequest;
import org.school.management.students.records.application.dto.response.RecordDocumentResponse;
import org.school.management.students.records.application.usecases.AddDocumentToRecordUseCase;
import org.school.management.students.records.application.usecases.GetRecordByStudentIdUseCase;
import org.school.management.students.records.application.usecases.ReviewDocumentUseCase;
import org.school.management.students.records.application.usecases.UpdateRecordStatusUseCase;
import org.school.management.students.records.application.usecases.UploadRecordDocumentUseCase;
import org.school.management.students.records.infrastructure.web.dto.RecordWebDto;
import org.school.management.students.records.infrastructure.web.mapper.RecordWebMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/students/{studentId}/record")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Records", description = "Gestión del legajo digital del estudiante")
@SecurityRequirement(name = "bearerAuth")
public class RecordController {

    private final GetRecordByStudentIdUseCase    getRecordByStudentIdUseCase;
    private final AddDocumentToRecordUseCase     addDocumentToRecordUseCase;
    private final ReviewDocumentUseCase          reviewDocumentUseCase;
    private final UpdateRecordStatusUseCase      updateRecordStatusUseCase;
    private final UploadRecordDocumentUseCase    uploadRecordDocumentUseCase;
    private final RecordWebMapper                mapper;

    // ── GET /api/admin/students/{studentId}/record ────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener el legajo completo del estudiante")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> getRecord(
            @PathVariable UUID studentId,
            @AuthenticationPrincipal UserPrincipal principal) {

        log.debug("GET record — studentId: {}, requestedBy: {}",
                studentId, SecurityContextHelper.extractUserId(principal));

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        getRecordByStudentIdUseCase.execute(studentId)
                )
        );
    }

    // ── POST /api/admin/students/{studentId}/record/documents (JSON) ──────
    @PostMapping("/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Agregar un documento al legajo (sin archivo)")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> addDocument(
            @PathVariable UUID studentId,
            @Valid @RequestBody RecordWebDto.AddDocumentWebRequest webRequest,
            @AuthenticationPrincipal UserPrincipal principal) {

        UUID uploadedBy = SecurityContextHelper.extractUserId(principal);
        log.debug("POST document — studentId: {}, uploadedBy: {}", studentId, uploadedBy);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toWebResponse(
                        addDocumentToRecordUseCase.execute(
                                studentId,
                                mapper.toApplicationRequest(webRequest),
                                uploadedBy
                        )
                )
        );
    }

    // ── POST /api/admin/students/{studentId}/record/{recordId}/upload ─────
    @PostMapping(
            value = "/{recordId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
            summary = "Subir archivo al legajo",
            description = "Sube un PDF o imagen (JPG/PNG, máx 10 MB) y lo asocia al legajo."
    )
    public ResponseEntity<RecordDocumentResponse> uploadDocument(
            @PathVariable UUID studentId,
            @PathVariable UUID recordId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("documentTypeId") UUID documentTypeId,
            @RequestPart("title") String title,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "issueDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestPart(value = "expiryDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestPart(value = "issuingAuthority", required = false) String issuingAuthority,
            @AuthenticationPrincipal UserPrincipal principal) {

        log.info("POST upload — studentId: {}, recordId: {}, file: {}",
                studentId, recordId, file.getOriginalFilename());

        var request = new UploadDocumentRequest(
                documentTypeId, title, description,
                issueDate, expiryDate, issuingAuthority
        );

        RecordDocumentResponse response = uploadRecordDocumentUseCase.execute(
                recordId, request, file, SecurityContextHelper.extractUserId(principal)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── PATCH /api/admin/students/{studentId}/record/documents/{documentId}
    @PatchMapping("/documents/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Aprobar o rechazar un documento del legajo")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> reviewDocument(
            @PathVariable UUID studentId,
            @PathVariable UUID documentId,
            @RequestParam String action,
            @RequestParam(required = false) String observations,
            @AuthenticationPrincipal UserPrincipal principal) {

        log.debug("PATCH document — studentId: {}, documentId: {}, action: {}",
                studentId, documentId, action);

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        reviewDocumentUseCase.execute(
                                studentId,
                                documentId,
                                action,
                                observations
                        )
                )
        );
    }

    // ── PATCH /api/admin/students/{studentId}/record/status ───────────────
    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado del legajo — SUBMIT, APPROVE o REJECT")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> updateRecordStatus(
            @PathVariable UUID studentId,
            @Valid @RequestBody RecordWebDto.UpdateRecordStatusWebRequest webRequest,
            @AuthenticationPrincipal UserPrincipal principal) {

        UUID reviewedBy = SecurityContextHelper.extractUserId(principal);
        log.debug("PATCH record status — studentId: {}, action: {}, reviewedBy: {}",
                studentId, webRequest.recordAction(), reviewedBy);

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        updateRecordStatusUseCase.execute(
                                studentId,
                                mapper.toApplicationRequest(webRequest),
                                reviewedBy
                        )
                )
        );
    }


}