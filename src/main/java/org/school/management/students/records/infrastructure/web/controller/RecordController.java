package org.school.management.students.records.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.students.records.application.usecases.AddDocumentToRecordUseCase;
import org.school.management.students.records.application.usecases.GetRecordByStudentIdUseCase;
import org.school.management.students.records.application.usecases.ReviewDocumentUseCase;
import org.school.management.students.records.application.usecases.UpdateRecordStatusUseCase;
import org.school.management.students.records.infrastructure.web.dto.RecordWebDto;
import org.school.management.students.records.infrastructure.web.mapper.RecordWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/students/{studentId}/record")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Records", description = "Gestión del legajo digital del estudiante")
@SecurityRequirement(name = "bearerAuth")
public class RecordController {

    private final GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;
    private final AddDocumentToRecordUseCase addDocumentToRecordUseCase;
    private final ReviewDocumentUseCase reviewDocumentUseCase;
    private final UpdateRecordStatusUseCase updateRecordStatusUseCase;
    private final RecordWebMapper mapper;

    // ── GET /api/admin/students/{studentId}/record ────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener el legajo completo del estudiante")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> getRecord(
            @PathVariable UUID studentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("GET record — studentId: {}, requestedBy: {}",
                studentId, extractUserId(userDetails));

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        getRecordByStudentIdUseCase.execute(studentId)
                )
        );
    }

    // ── POST /api/admin/students/{studentId}/record/documents ─────────────
    @PostMapping("/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Agregar un documento al legajo")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> addDocument(
            @PathVariable UUID studentId,
            @Valid @RequestBody RecordWebDto.AddDocumentWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID uploadedBy = extractUserId(userDetails);
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

    // ── PATCH /api/admin/students/{studentId}/record/documents/{documentId}
    @PatchMapping("/documents/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Aprobar o rechazar un documento del legajo")
    public ResponseEntity<RecordWebDto.StudentRecordWebResponse> reviewDocument(
            @PathVariable UUID studentId,
            @PathVariable UUID documentId,
            @RequestParam String action,
            @RequestParam(required = false) String observations,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("PATCH document — studentId: {}, documentId: {}, action: {}, reviewedBy: {}",
                studentId, documentId, action, extractUserId(userDetails));

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
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID reviewedBy = extractUserId(userDetails);
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

    // ── Utilidad ──────────────────────────────────────────────────────────
    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }
        throw new IllegalStateException(
                "Principal inesperado: " + userDetails.getClass().getName()
        );
    }
}