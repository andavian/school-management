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
// import org.school.management.resources.application.usecases.CreateResourceUseCase;
// import org.school.management.resources.application.usecases.GetResourceByIdUseCase;
// import org.school.management.resources.application.usecases.ListResourcesUseCase;
// import org.school.management.resources.application.usecases.UpdateResourceUseCase;
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
@Tag(name = "Resources", description = "Gestión de catálogo de recursos didácticos y unidades físicas")
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    // Use Cases de Catálogo (pendientes de implementación trivial si no existen)
    // private final CreateResourceUseCase createResourceUseCase;
    // private final GetResourceByIdUseCase getResourceByIdUseCase;
    // private final ListResourcesUseCase listResourcesUseCase;
    // private final UpdateResourceUseCase updateResourceUseCase;

    private final CreateResourceUnitUseCase createResourceUnitUseCase;
    private final UpdateUnitStatusUseCase updateUnitStatusUseCase;
    private final ResourcesWebMapper webMapper;

    // ─── UNIDADES FÍSICAS ─────────────────────────────────────────────────

    @PostMapping("/{resourceId}/units")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceUnitWebResponse> createUnit(
            @PathVariable UUID resourceId,
            @Valid @RequestBody ResourceWebDto.CreateResourceUnitWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);

        var appRequest = new org.school.management.resources.application.dto.request.CreateResourceUnitRequest(
                resourceId, request.unitCode(), request.serialNumber(), request.conditionStatus()
        );

        ResourceUnitResponse response = createResourceUnitUseCase.execute(appRequest, actorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toResourceUnitWebResponse(response));
    }

    @PatchMapping("/units/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceUnitWebResponse> updateUnit(
            @PathVariable UUID unitId,
            @Valid @RequestBody ResourceWebDto.UpdateUnitStatusWebRequest request) {

        var appRequest = new org.school.management.resources.application.dto.request.UpdateUnitStatusRequest(
                request.unitStatus(), request.conditionStatus(), request.notes()
        );

        ResourceUnitResponse response = updateUnitStatusUseCase.execute(unitId, appRequest);
        return ResponseEntity.ok(webMapper.toResourceUnitWebResponse(response));
    }

    // ─── CATÁLOGO DE RECURSOS (Estructura lista para conectar Use Cases) ─
    /*
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> createResource(...) { ... }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> getResource(...) { ... }

    @GetMapping
    public ResponseEntity<List<ResourceWebDto.ResourceWebResponse>> listResources(...) { ... }

    @PatchMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResourceWebDto.ResourceWebResponse> updateResource(...) { ... }
    */
}