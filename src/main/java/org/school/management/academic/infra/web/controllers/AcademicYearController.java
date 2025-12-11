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
import org.school.management.academic.application.dto.request.*;
import org.school.management.academic.application.dto.response.*;
import org.school.management.academic.application.usecases.year.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/academic-years")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Academic Years",
        description = "Gestión de ciclos lectivos del sistema (crear, activar, cerrar, consultar)"
)
@SecurityRequirement(name = "bearer-jwt") // Seguridad declarada a nivel de clase
public class AcademicYearController {

    private final CreateAcademicYearUseCase createAcademicYearUseCase;
    private final GetAcademicYearUseCase getAcademicYearUseCase;
    private final ListAcademicYearsUseCase listAcademicYearsUseCase;
    private final ActivateAcademicYearUseCase activateAcademicYearUseCase;
    private final CloseAcademicYearUseCase closeAcademicYearUseCase;
    private final GetCurrentAcademicYearUseCase getCurrentAcademicYearUseCase;

    // ============================================================
    // CREATE ACADEMIC YEAR
    // ============================================================

    @Operation(
            summary = "Crear ciclo lectivo",
            description = "Crea un nuevo ciclo lectivo. Solo accesible para ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Ciclo lectivo creado exitosamente",
                    content = @Content(schema = @Schema(implementation = AcademicYearResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El ciclo lectivo ya existe")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponse> createAcademicYear(
            @Valid @RequestBody CreateAcademicYearRequest request) {

        log.info("POST /api/admin/academic-years - Crear ciclo lectivo {}", request.year());

        AcademicYearResponse response = createAcademicYearUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // LIST ACADEMIC YEARS
    // ============================================================

    @Operation(
            summary = "Listar ciclos lectivos",
            description = "Devuelve todos los ciclos lectivos ordenados por año descendente."
    )
    @ApiResponse(responseCode = "200", description = "Lista de ciclos lectivos devuelta correctamente")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    public ResponseEntity<List<AcademicYearResponse>> listAcademicYears() {

        log.debug("GET /api/admin/academic-years - Listar ciclos lectivos");

        return ResponseEntity.ok(listAcademicYearsUseCase.execute());
    }

    // ============================================================
    // GET CURRENT ACADEMIC YEAR
    // ============================================================

    @Operation(
            summary = "Obtener ciclo lectivo actual",
            description = "Devuelve el ciclo lectivo actualmente activo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ciclo actual obtenido"),
            @ApiResponse(responseCode = "404", description = "No hay ciclo lectivo activo")
    })
    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF','STUDENT','PARENT')")
    public ResponseEntity<AcademicYearResponse> getCurrentAcademicYear() {

        log.debug("GET /api/admin/academic-years/current - Ciclo actual");

        return ResponseEntity.ok(getCurrentAcademicYearUseCase.execute());
    }

    // ============================================================
    // GET ACADEMIC YEAR BY ID
    // ============================================================

    @Operation(
            summary = "Obtener ciclo lectivo por ID",
            description = "Devuelve un ciclo lectivo específico por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ciclo lectivo encontrado"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    public ResponseEntity<AcademicYearResponse> getAcademicYear(
            @Parameter(description = "ID del ciclo lectivo", example = "2024-uuid")
            @PathVariable String id) {

        log.debug("GET /api/admin/academic-years/{} - Obtener ciclo lectivo", id);

        return ResponseEntity.ok(getAcademicYearUseCase.execute(id));
    }

    // ============================================================
    // ACTIVATE ACADEMIC YEAR
    // ============================================================

    @Operation(
            summary = "Activar ciclo lectivo",
            description = """
            Activa un ciclo lectivo.  
            Si había un ciclo activo previamente, se desactiva automáticamente.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ciclo lectivo activado"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "409", description = "El ciclo ya está activo")
    })
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponse> activateAcademicYear(
            @Parameter(description = "ID del ciclo lectivo")
            @PathVariable String id) {

        log.info("PUT /api/admin/academic-years/{}/activate - Activar ciclo lectivo", id);

        return ResponseEntity.ok(activateAcademicYearUseCase.execute(id));
    }

    // ============================================================
    // CLOSE ACADEMIC YEAR
    // ============================================================

    @Operation(
            summary = "Cerrar ciclo lectivo",
            description = "Cierra un ciclo lectivo. No puede volverse a abrir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ciclo lectivo cerrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo no encontrado")
    })
    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponse> closeAcademicYear(
            @Parameter(description = "ID del ciclo lectivo")
            @PathVariable String id) {

        log.info("PUT /api/admin/academic-years/{}/close - Cerrar ciclo lectivo", id);

        return ResponseEntity.ok(closeAcademicYearUseCase.execute(id));
    }
}
