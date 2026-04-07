package org.school.management.resources.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.usecases.CreateResourceUnitUseCase;
import org.school.management.resources.application.usecases.UpdateUnitStatusUseCase;
import org.school.management.resources.infrastructure.web.dto.ResourceWebDto;
import org.school.management.resources.infrastructure.web.mapper.ResourcesWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador para la gestión del Catálogo de Recursos y sus Unidades Físicas.
 * Nota: Para el CRUD del Catálogo (CreateResource, GetResource, etc.), se asume
 * la existencia de los UseCases correspondientes (CreateResourceUseCase, etc.)
 * inyectados aquí.
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor @Slf4j @Validated
@Tag(name = "Resources") @SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    // Use Cases para Catálogo (Asumidos/Inyectados)
    private final CreateResourceUnitUseCase createResourceUnitUseCase;
    private final UpdateUnitStatusUseCase updateUnitStatusUseCase;
    // private final CreateResourceUseCase createResourceUseCase;
    // private final GetResourceByIdUseCase getResourceByIdUseCase;

    private final ResourcesWebMapper webMapper;

    // ─── UNIDADES FÍSICAS ─────────────────────────────────────────────────

    @PostMapping("/{resourceId}/units")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceUnitWebResponse> createUnit(
            @PathVariable UUID resourceId,
            @Valid @RequestBody ResourceWebDto.CreateResourceUnitWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);

        // Mapear Web Request a App Request
        var appRequest = new org.school.management.resources.application.dto.request.CreateResourceUnitRequest(
                resourceId, request.unitCode(), request.serialNumber(), request.conditionStatus()
        );

        ResourceUnitResponse response = createResourceUnitUseCase.execute(appRequest, actorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toResourceUnitWebResponse(response));
    }

    @PatchMapping("/units/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceUnitWebResponse> updateUnitStatus(
            @PathVariable UUID unitId,
            @Valid @RequestBody ResourceWebDto.UpdateUnitStatusWebRequest request) {

        var appRequest = new org.school.management.resources.application.dto.request.UpdateUnitStatusRequest(
                request.unitStatus(), request.conditionStatus(), request.notes()
        );

        ResourceUnitResponse response = updateUnitStatusUseCase.execute(unitId, appRequest);
        return ResponseEntity.ok(webMapper.toResourceUnitWebResponse(response));
    }

    // ─── CATÁLOGO DE RECURSOS (Ejemplos) ─────────────────────────────────
    // Para que este código compile, debes inyectar los Use Cases correspondientes
    // y descomentar las llamadas.

    /*
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> createResource(
            @Valid @RequestBody ResourceWebDto.CreateResourceWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = SecurityContextHelper.extractUserId(userDetails);
        ResourceResponse response = createResourceUseCase.execute(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toResourceWebResponse(response));
    }
    */
}