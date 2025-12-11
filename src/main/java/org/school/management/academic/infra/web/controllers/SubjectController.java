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
import org.school.management.academic.application.dto.request.CreateSubjectRequest;
import org.school.management.academic.application.dto.request.UpdateSubjectRequest;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.usecases.subject.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subjects")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Subjects",
        description = "Gestión de materias (subjects) del sistema académico"
)
@SecurityRequirement(name = "bearer-jwt")
public class SubjectController {

    private final CreateSubjectUseCase createSubjectUseCase;
    private final GetSubjectUseCase getSubjectUseCase;
    private final ListSubjectsUseCase listSubjectsUseCase;
    private final ListSubjectsForGradeLevelUseCase listSubjectsForGradeLevelUseCase;
    private final UpdateSubjectUseCase updateSubjectUseCase;

    // ============================================================
    // CREATE SUBJECT
    // ============================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear materia",
            description = """
                Crea una nueva materia dentro de la estructura académica.
                Las materias pueden ser de formación común o vinculadas a una orientación técnica.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Materia creada correctamente",
                    content = @Content(schema = @Schema(implementation = SubjectResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Orientación no encontrada"),
            @ApiResponse(responseCode = "409", description = "El código de materia ya existe")
    })
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody CreateSubjectRequest request) {

        log.info("POST /api/admin/subjects - Crear materia: {}", request.name());

        SubjectResponse response = createSubjectUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // LIST SUBJECTS
    // ============================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Listar materias",
            description = """
                Devuelve las materias disponibles.  
                Se puede filtrar por:
                - Año escolar (`yearLevel`)
                - Orientación técnica (`orientationId`)
                - Estado activo (`activeOnly`)
                """
    )
    @ApiResponse(responseCode = "200", description = "Materias obtenidas correctamente")
    public ResponseEntity<List<SubjectResponse>> listSubjects(
            @Parameter(description = "Año para filtrar (1-7)", example = "5")
            @RequestParam(required = false) Integer yearLevel,

            @Parameter(description = "ID de la orientación para filtrar", example = "orientation-uuid")
            @RequestParam(required = false) String orientationId,

            @Parameter(description = "Filtrar solo materias activas", example = "true")
            @RequestParam(required = false) Boolean activeOnly) {

        log.debug(
                "GET /api/admin/subjects - Listado (yearLevel={}, orientationId={}, activeOnly={})",
                yearLevel, orientationId, activeOnly
        );

        List<SubjectResponse> response =
                listSubjectsUseCase.execute(yearLevel, orientationId, activeOnly);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // LIST SUBJECTS BY GRADE LEVEL
    // ============================================================

    @GetMapping("/grade-level/{gradeLevelId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Listar materias por curso",
            description = """
                Devuelve las materias correspondientes a un curso específico (grade level),  
                combinando las materias comunes con las específicas de la orientación.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Materias obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<List<SubjectResponse>> listSubjectsForGradeLevel(
            @Parameter(description = "ID del curso (grade level)", example = "grade-level-uuid", required = true)
            @PathVariable String gradeLevelId) {

        log.debug("GET /api/admin/subjects/grade-level/{} - Listar materias", gradeLevelId);

        List<SubjectResponse> response = listSubjectsForGradeLevelUseCase.execute(gradeLevelId);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // GET SUBJECT BY ID
    // ============================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Obtener materia por ID",
            description = "Devuelve una materia específica según su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Materia encontrada"),
            @ApiResponse(responseCode = "404", description = "Materia no encontrada")
    })
    public ResponseEntity<SubjectResponse> getSubject(
            @Parameter(description = "ID de la materia", example = "subject-uuid")
            @PathVariable String id) {

        log.debug("GET /api/admin/subjects/{} - Obtener materia", id);

        SubjectResponse response = getSubjectUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // UPDATE SUBJECT
    // ============================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Actualizar materia",
            description = """
                Actualiza los datos de una materia existente,  
                incluyendo su nombre, código, carga horaria u orientación.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Materia actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Materia no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<SubjectResponse> updateSubject(
            @Parameter(description = "ID de la materia a actualizar", example = "subject-uuid")
            @PathVariable String id,

            @Valid @RequestBody UpdateSubjectRequest request) {

        log.info("PUT /api/admin/subjects/{} - Actualizar materia", id);

        SubjectResponse response = updateSubjectUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}
