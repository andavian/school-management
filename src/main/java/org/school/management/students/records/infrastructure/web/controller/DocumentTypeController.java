package org.school.management.students.records.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.students.records.application.usecases.*;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.infrastructure.web.dto.DocumentTypeWebDto;
import org.school.management.students.records.infrastructure.web.mapper.DocumentTypeWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/document-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Document Types", description = "Catálogo de tipos de documentos del legajo")
@SecurityRequirement(name = "bearerAuth")
public class DocumentTypeController {

    private final GetDocumentTypesUseCase         getDocumentTypesUseCase;
    private final GetDocumentTypeByIdUseCase       getDocumentTypeByIdUseCase;
    private final CreateDocumentTypeUseCase        createDocumentTypeUseCase;
    private final UpdateDocumentTypeUseCase        updateDocumentTypeUseCase;
    private final ToggleDocumentTypeStatusUseCase  toggleDocumentTypeStatusUseCase;
    private final DocumentTypeWebMapper            mapper;

    // ── GET /api/admin/document-types ────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
            summary = "Listar tipos de documento",
            description = "Soporta filtros: onlyActive (default true), category, onlyMandatory"
    )
    public ResponseEntity<List<DocumentTypeWebDto.DocumentTypeWebResponse>> getAll(
            @RequestParam(defaultValue = "true")  boolean onlyActive,
            @RequestParam(required = false)        DocumentCategory category,
            @RequestParam(required = false)        Boolean onlyMandatory) {

        var responses = getDocumentTypesUseCase
                .execute(onlyActive, category, onlyMandatory)
                .stream()
                .map(mapper::toWebResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ── GET /api/admin/document-types/{documentTypeId} ───────────────────
    @GetMapping("/{documentTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener un tipo de documento por ID")
    public ResponseEntity<DocumentTypeWebDto.DocumentTypeWebResponse> getById(
            @PathVariable UUID documentTypeId) {

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        getDocumentTypeByIdUseCase.execute(documentTypeId)
                )
        );
    }

    // ── POST /api/admin/document-types ───────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo tipo de documento en el catálogo")
    public ResponseEntity<DocumentTypeWebDto.DocumentTypeWebResponse> create(
            @Valid @RequestBody DocumentTypeWebDto.CreateDocumentTypeWebRequest webRequest,
            @AuthenticationPrincipal UserDetails principal) {

        log.debug("POST document-type — code: {}, requestedBy: {}",
                webRequest.code(), SecurityContextHelper.extractUserId(principal));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toWebResponse(
                        createDocumentTypeUseCase.execute(
                                mapper.toApplicationRequest(webRequest)
                        )
                )
        );
    }

    // ── PATCH /api/admin/document-types/{documentTypeId} ─────────────────
    @PatchMapping("/{documentTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar nombre, categoría, descripción y vigencia de un tipo de documento")
    public ResponseEntity<DocumentTypeWebDto.DocumentTypeWebResponse> update(
            @PathVariable UUID documentTypeId,
            @Valid @RequestBody DocumentTypeWebDto.UpdateDocumentTypeWebRequest webRequest,
            @AuthenticationPrincipal UserDetails principal) {

        log.debug("PATCH document-type — id: {}, requestedBy: {}",
                documentTypeId, SecurityContextHelper.extractUserId(principal));

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        updateDocumentTypeUseCase.execute(
                                documentTypeId,
                                mapper.toApplicationRequest(webRequest)
                        )
                )
        );
    }

    // ── PATCH /api/admin/document-types/{documentTypeId}/activate ────────
    @PatchMapping("/{documentTypeId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar un tipo de documento")
    public ResponseEntity<DocumentTypeWebDto.DocumentTypeWebResponse> activate(
            @PathVariable UUID documentTypeId,
            @AuthenticationPrincipal UserDetails principal) {

        log.debug("PATCH activate document-type — id: {}, requestedBy: {}",
                documentTypeId, SecurityContextHelper.extractUserId(principal));

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        toggleDocumentTypeStatusUseCase.execute(documentTypeId, true)
                )
        );
    }

    // ── PATCH /api/admin/document-types/{documentTypeId}/deactivate ───────
    @PatchMapping("/{documentTypeId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar un tipo de documento")
    public ResponseEntity<DocumentTypeWebDto.DocumentTypeWebResponse> deactivate(
            @PathVariable UUID documentTypeId,
            @AuthenticationPrincipal UserDetails principal) {

        log.debug("PATCH deactivate document-type — id: {}, requestedBy: {}",
                documentTypeId, SecurityContextHelper.extractUserId(principal));

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        toggleDocumentTypeStatusUseCase.execute(documentTypeId, false)
                )
        );
    }
}