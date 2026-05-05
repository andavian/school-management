package org.school.management.academic.infra.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.InitRegistrySequenceRequest;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.usecases.GetQualificationRegistryUseCase;
import org.school.management.academic.application.usecases.qualification_registry.*;
import org.school.management.academic.infra.web.dto.QualificationRegistryWebDto.CreateQualificationRegistryWebRequest;
import org.school.management.academic.infra.web.dto.QualificationRegistryWebDto.InitRegistrySequenceWebRequest;
import org.school.management.academic.infra.web.dto.QualificationRegistryWebDto.QualificationRegistryWebResponse;
import org.school.management.academic.infra.web.mappers.QualificationRegistryWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/qualification-registries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Qualification Registries", description = "Gestión de libros de calificaciones")
@SecurityRequirement(name = "bearerAuth")
public class QualificationRegistryController {

    private final InitializeRegistrySequenceUseCase initializeSequenceUseCase;
    private final CreateQualificationRegistryUseCase createRegistryUseCase;
    private final CloseQualificationRegistryUseCase closeRegistryUseCase;
    private final ReactivateQualificationRegistryUseCase reactivateRegistryUseCase;
    private final GetQualificationRegistryUseCase getRegistryUseCase;
    private final SearchQualificationRegistriesUseCase searchRegistriesUseCase;
    private final QualificationRegistryWebMapper webMapper;

    // ========================================================================
    // SEED (solo si no hay registros previos)
    // ========================================================================
    @PostMapping("/seed-sequence")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inicializar la secuencia de libros",
            description = "Crea el primer libro usando el último número en papel. Solo funciona si no hay libros en el sistema.")
    public ResponseEntity<QualificationRegistryWebResponse> seedSequence(
            @Valid @RequestBody InitRegistrySequenceWebRequest webRequest) {
        log.info("REST request to seed registry sequence with last paper number: {}", webRequest.lastPaperNumber());
        InitRegistrySequenceRequest appRequest = webMapper.toInitRegistrySequenceRequest(webRequest);
        QualificationRegistryResponse response = initializeSequenceUseCase.execute(appRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toQualificationRegistryWebResponse(response));
    }

    // ========================================================================
    // CREACIÓN MANUAL
    // ========================================================================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo libro manualmente",
            description = "Crea un libro activo con número correlativo para el año académico indicado.")
    public ResponseEntity<QualificationRegistryWebResponse> create(
            @Valid @RequestBody CreateQualificationRegistryWebRequest webRequest) {
        log.info("REST request to create qualification registry for academic year: {}", webRequest.academicYearId());
        var appRequest = webMapper.toCreateQualificationRegistryRequest(webRequest);
        QualificationRegistryResponse response = createRegistryUseCase.execute(appRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toQualificationRegistryWebResponse(response));
    }

    // ========================================================================
    // CONSULTAS
    // ========================================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Listar libros de calificaciones",
            description = "Devuelve todos los libros, opcionalmente filtrados por año académico y estado.")
    public ResponseEntity<List<QualificationRegistryWebResponse>> search(
            @RequestParam(required = false) String academicYearId,
            @RequestParam(required = false) String status) {
        log.info("REST request to search qualification registries — academicYearId: {}, status: {}", academicYearId, status);
        List<QualificationRegistryResponse> responses = searchRegistriesUseCase.execute(academicYearId, status);
        List<QualificationRegistryWebResponse> webResponses = responses.stream()
                .map(webMapper::toQualificationRegistryWebResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(webResponses);
    }

    @GetMapping("/{registryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener un libro por ID")
    public ResponseEntity<QualificationRegistryWebResponse> getById(@PathVariable String registryId) {
        log.info("REST request to get qualification registry: {}", registryId);
        QualificationRegistryResponse response = getRegistryUseCase.execute(registryId);
        return ResponseEntity.ok(webMapper.toQualificationRegistryWebResponse(response));
    }

    // ========================================================================
    // CERRAR
    // ========================================================================
    @PatchMapping("/{registryId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cerrar un libro manualmente")
    public ResponseEntity<QualificationRegistryWebResponse> close(@PathVariable String registryId) {
        log.info("REST request to close qualification registry: {}", registryId);
        QualificationRegistryResponse response = closeRegistryUseCase.execute(registryId);
        return ResponseEntity.ok(webMapper.toQualificationRegistryWebResponse(response));
    }

    // ========================================================================
    // REACTIVAR
    // ========================================================================
    @PatchMapping("/{registryId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivar un libro cerrado")
    public ResponseEntity<QualificationRegistryWebResponse> reactivate(@PathVariable String registryId) {
        log.info("REST request to reactivate qualification registry: {}", registryId);
        QualificationRegistryResponse response = reactivateRegistryUseCase.execute(registryId);
        return ResponseEntity.ok(webMapper.toQualificationRegistryWebResponse(response));
    }
}