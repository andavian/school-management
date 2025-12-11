package org.school.management.academic.infra.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.CreateOrientationRequest;
import org.school.management.academic.application.dto.request.UpdateOrientationRequest;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.usecases.orientation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orientations")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Orientations",
        description = "Gestión de orientaciones técnicas para el nivel secundario"
)
@SecurityRequirement(name = "bearer-jwt")
public class OrientationController {

    private final CreateOrientationUseCase createOrientationUseCase;
    private final GetOrientationUseCase getOrientationUseCase;
    private final ListOrientationsUseCase listOrientationsUseCase;
    private final ListOrientationsForYearLevelUseCase listOrientationsForYearLevelUseCase;
    private final UpdateOrientationUseCase updateOrientationUseCase;
    private final ToggleOrientationStatusUseCase toggleOrientationStatusUseCase;

    // ============================================================
    // CREATE ORIENTATION
    // ============================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear orientación técnica",
            description = """
                Crea una nueva orientación técnica del nivel secundario.
                Las orientaciones se aplican entre 4º y 7º año.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Orientación creada correctamente",
                    content = @Content(schema = @Schema(implementation = OrientationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El código de orientación ya existe")
    })
    public ResponseEntity<OrientationResponse> createOrientation(
            @Valid @RequestBody CreateOrientationRequest request) {

        log.info("POST /api/admin/orientations - Crear orientación: {}", request.name());

        OrientationResponse response = createOrientationUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // LIST ORIENTATIONS
    // ============================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Listar orientaciones",
            description = """
                Devuelve todas las orientaciones disponibles.
                Puede filtrarse por estado activo mediante `activeOnly`.
                """
    )
    @ApiResponse(responseCode = "200", description = "Orientaciones obtenidas correctamente")
    public ResponseEntity<List<OrientationResponse>> listOrientations(
            @Parameter(
                    description = "Filtrar solo orientaciones activas",
                    example = "true"
            )
            @RequestParam(required = false) Boolean activeOnly) {

        log.debug("GET /api/admin/orientations - Listado (activeOnly={})", activeOnly);

        List<OrientationResponse> response = listOrientationsUseCase.execute(activeOnly);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // LIST ORIENTATIONS BY YEAR LEVEL
    // ============================================================

    @GetMapping("/year-level/{yearLevel}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Listar orientaciones por año",
            description = """
                Devuelve las orientaciones habilitadas para un año específico
                entre 4° y 7° del nivel medio técnico.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orientaciones obtenidas"),
            @ApiResponse(responseCode = "400", description = "Año fuera del rango permitido")
    })
    public ResponseEntity<List<OrientationResponse>> listOrientationsForYearLevel(
            @Parameter(description = "Año escolar (4-7)", example = "6")
            @PathVariable Integer yearLevel) {

        log.debug("GET /api/admin/orientations/year-level/{} - Listar orientaciones", yearLevel);

        List<OrientationResponse> response = listOrientationsForYearLevelUseCase.execute(yearLevel);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // GET ORIENTATION BY ID
    // ============================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Obtener orientación",
            description = "Devuelve una orientación por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orientación encontrada"),
            @ApiResponse(responseCode = "404", description = "Orientación no encontrada")
    })
    public ResponseEntity<OrientationResponse> getOrientation(
            @Parameter(description = "ID de la orientación", example = "orientation-uuid")
            @PathVariable String id) {

        log.debug("GET /api/admin/orientations/{} - Obtener orientación", id);

        OrientationResponse response = getOrientationUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // UPDATE ORIENTATION
    // ============================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Actualizar orientación",
            description = "Actualiza los datos de una orientación existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orientación actualizada"),
            @ApiResponse(responseCode = "404", description = "No encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<OrientationResponse> updateOrientation(
            @Parameter(description = "ID de la orientación a actualizar")
            @PathVariable String id,

            @Valid @RequestBody UpdateOrientationRequest request) {

        log.info("PUT /api/admin/orientations/{} - Actualizar orientación", id);

        OrientationResponse response = updateOrientationUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // TOGGLE ORIENTATION STATUS
    // ============================================================

    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Activar/desactivar orientación",
            description = "Activa o desactiva una orientación según su estado actual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    public ResponseEntity<OrientationResponse> toggleOrientationStatus(
            @Parameter(description = "ID de orientación a activar/desactivar")
            @PathVariable String id) {

        log.info("PUT /api/admin/orientations/{}/toggle-status - Cambiar estado", id);

        OrientationResponse response = toggleOrientationStatusUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
