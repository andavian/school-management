package org.school.management.students.parents.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.students.parents.application.usecases.CreateParentUseCase;
import org.school.management.students.parents.application.usecases.GetParentsByStudentIdUseCase;
import org.school.management.students.parents.application.usecases.LinkParentToStudentUseCase;
import org.school.management.students.parents.application.usecases.UpdateParentUseCase;
import org.school.management.students.parents.infrastructure.web.dto.ParentWebDto;
import org.school.management.students.parents.infrastructure.web.mapper.ParentWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Parents", description = "Gestión de padres/tutores y sus vínculos con estudiantes")
@SecurityRequirement(name = "bearerAuth")
public class ParentController {

    private final CreateParentUseCase createParentUseCase;
    private final UpdateParentUseCase updateParentUseCase;
    private final GetParentsByStudentIdUseCase getParentsByStudentIdUseCase;
    private final LinkParentToStudentUseCase linkParentToStudentUseCase;
    private final ParentWebMapper mapper;

    // ── POST /api/admin/parents ───────────────────────────────────────────
    @PostMapping("/api/admin/parents")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Crear un nuevo padre/tutor en el sistema")
    public ResponseEntity<ParentWebDto.ParentWebResponse> createParent(
            @Valid @RequestBody ParentWebDto.CreateParentWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {


        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toWebResponse(
                        createParentUseCase.execute(
                                mapper.toApplicationRequest(webRequest),
                                SecurityContextHelper.extractUserId(userDetails)
                        )
                )
        );
    }

    // ── PATCH /api/admin/parents/{parentId} ───────────────────────────────
    @PatchMapping("/api/admin/parents/{parentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Actualizar datos del padre/tutor")
    public ResponseEntity<ParentWebDto.ParentWebResponse> updateParent(
            @PathVariable UUID parentId,
            @Valid @RequestBody ParentWebDto.UpdateParentWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("PATCH parent — parentId: {}, requestedBy: {}",
                parentId, SecurityContextHelper.extractUserId(userDetails));

        return ResponseEntity.ok(
                mapper.toWebResponse(
                        updateParentUseCase.execute(
                                parentId,
                                mapper.toApplicationRequest(webRequest)
                        )
                )
        );
    }

    // ── GET /api/admin/students/{studentId}/parents ───────────────────────
    @GetMapping("/api/admin/students/{studentId}/parents")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener todos los padres/tutores de un estudiante")
    public ResponseEntity<List<ParentWebDto.StudentParentWebResponse>> getParentsByStudent(
            @PathVariable UUID studentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("GET parents — studentId: {}, requestedBy: {}",
                studentId, SecurityContextHelper.extractUserId(userDetails));

        List<ParentWebDto.StudentParentWebResponse> response =
                getParentsByStudentIdUseCase.execute(studentId)
                        .stream()
                        .map(mapper::toStudentParentWebResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // ── POST /api/admin/students/{studentId}/parents ──────────────────────
    @PostMapping("/api/admin/students/{studentId}/parents")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Vincular un padre/tutor existente a un estudiante")
    public ResponseEntity<ParentWebDto.StudentParentWebResponse> linkParentToStudent(
            @PathVariable UUID studentId,
            @Valid @RequestBody ParentWebDto.LinkParentWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("POST link parent — studentId: {}, parentDni: {}, requestedBy: {}",
                studentId, webRequest.parentDni(), SecurityContextHelper.extractUserId(userDetails));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toStudentParentWebResponse(
                        linkParentToStudentUseCase.execute(
                                studentId,
                                mapper.toApplicationRequest(webRequest)
                        )
                )
        );
    }


}