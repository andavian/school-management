package org.school.management.resources.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.resources.application.dto.request.CreateResourceUnitRequest;
import org.school.management.resources.application.dto.request.UpdateResourceRequest;
import org.school.management.resources.application.dto.request.UpdateUnitStatusRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.usecases.*;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.school.management.resources.infrastructure.web.dto.ResourceWebDto;
import org.school.management.resources.infrastructure.web.mapper.ResourcesWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor @Slf4j @Validated
@Tag(name = "Resources", description = "Gestión de catálogo de recursos y unidades físicas")
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    private final CreateResourceUnitUseCase createResourceUnitUseCase;
    private final UpdateUnitStatusUseCase updateUnitStatusUseCase;
    private final ListResourcesUseCase listResourcesUseCase;
    private final GetResourceByIdUseCase getResourceByIdUseCase;
    private final CreateResourceUseCase createResourceUseCase;
    private final UpdateResourceUseCase updateResourceUseCase;

    private final ResourcesWebMapper webMapper;

    // ─── CRUD CATÁLOGO DE RECURSOS ───────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> createResource(
            @Valid @RequestBody ResourceWebDto.CreateResourceWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);

        ResourceResponse response = createResourceUseCase.execute(
                request.code(),
                request.name(),
                request.resourceType(),
                request.description(),
                request.location(),
                request.reservable(),
                request.notes(),
                actorId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toResourceWebResponse(response));
    }

    @GetMapping
    public ResponseEntity<List<ResourceWebDto.ResourceWebResponse>> listResources(
            @RequestParam(required = false) ResourceType type,
            @RequestParam(defaultValue = "false") boolean reservableOnly) {

        List<ResourceResponse> responses = listResourcesUseCase.execute(type, reservableOnly);
        List<ResourceWebDto.ResourceWebResponse> webResponses = responses.stream()
                .map(webMapper::toResourceWebResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(webResponses);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> getResource(@PathVariable UUID resourceId) {
        ResourceResponse response = getResourceByIdUseCase.execute(resourceId);
        return ResponseEntity.ok(webMapper.toResourceWebResponse(response));
    }

    @PatchMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> updateResource(
            @PathVariable UUID resourceId,
            @Valid @RequestBody ResourceWebDto.UpdateResourceWebRequest request) {

        // Mapeo Web Request -> Application Request
        UpdateResourceRequest appRequest = new UpdateResourceRequest(
                request.name(), request.description(), request.location(), request.reservable(), request.notes()
        );

        ResourceResponse response = updateResourceUseCase.execute(resourceId, appRequest);
        return ResponseEntity.ok(webMapper.toResourceWebResponse(response));
    }

    // ─── GESTIÓN DE UNIDADES FÍSICAS ─────────────────────────────────────

    @PostMapping("/{resourceId}/units")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceUnitWebResponse> createUnit(
            @PathVariable UUID resourceId,
            @Valid @RequestBody ResourceWebDto.CreateResourceUnitWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);

        // Mapeo Web Request -> Application Request
        CreateResourceUnitRequest appRequest = new CreateResourceUnitRequest(
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

        // Mapeo Web Request -> Application Request
        UpdateUnitStatusRequest appRequest = new UpdateUnitStatusRequest(
                request.unitStatus(), request.conditionStatus(), request.notes()
        );

        ResourceUnitResponse response = updateUnitStatusUseCase.execute(unitId, appRequest);
        return ResponseEntity.ok(webMapper.toResourceUnitWebResponse(response));
    }
}