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
import org.school.management.academic.application.dto.request.AssignHomeroomTeacherRequest;
import org.school.management.academic.application.dto.request.CreateGradeLevelRequest;
import org.school.management.academic.application.dto.response.GradeLevelResponse;
import org.school.management.academic.application.usecases.grade_level.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/grade-levels")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Grade Levels",
        description = "Gestión de cursos (grade levels) del sistema académico"
)
@SecurityRequirement(name = "bearer-jwt")
public class GradeLevelController {

    private final CreateGradeLevelUseCase createGradeLevelUseCase;
    private final GetGradeLevelUseCase getGradeLevelUseCase;
    private final ListGradeLevelsUseCase listGradeLevelsUseCase;
    private final AssignHomeroomTeacherUseCase assignHomeroomTeacherUseCase;
    private final DeactivateGradeLevelUseCase deactivateGradeLevelUseCase;

    // ============================================================
    // CREATE GRADE LEVEL
    // ============================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear curso",
            description = "Crea un nuevo curso (grade level) vinculado a un ciclo lectivo y orientación."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Curso creado exitosamente",
                    content = @Content(schema = @Schema(implementation = GradeLevelResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo u orientación no encontrados"),
            @ApiResponse(responseCode = "409", description = "El curso ya existe")
    })
    public ResponseEntity<GradeLevelResponse> createGradeLevel(
            @Valid @RequestBody CreateGradeLevelRequest request) {

        log.info("POST /api/admin/grade-levels - Crear curso {}{} (yearId = {})",
                request.yearLevel(), request.division(), request.academicYearId());

        GradeLevelResponse response = createGradeLevelUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // LIST GRADE LEVELS
    // ============================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Listar cursos",
            description = """
                Retorna los cursos existentes, con filtros opcionales:
                - academicYearId: filtra por ciclo lectivo  
                - activeOnly: devuelve solo cursos activos
                """
    )
    @ApiResponse(responseCode = "200", description = "Cursos obtenidos exitosamente")
    public ResponseEntity<List<GradeLevelResponse>> listGradeLevels(
            @Parameter(description = "Filtrar por ID del ciclo lectivo", example = "2024-uuid")
            @RequestParam(required = false) String academicYearId,

            @Parameter(description = "Filtrar solo activos", example = "true")
            @RequestParam(required = false) Boolean activeOnly) {

        log.debug("GET /api/admin/grade-levels - Listar cursos (academicYearId={}, activeOnly={})",
                academicYearId, activeOnly);

        List<GradeLevelResponse> response = listGradeLevelsUseCase.execute(academicYearId, activeOnly);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // GET GRADE LEVEL BY ID
    // ============================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STAFF')")
    @Operation(
            summary = "Obtener curso por ID",
            description = "Devuelve los detalles de un curso específico por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<GradeLevelResponse> getGradeLevel(
            @Parameter(description = "ID del curso", example = "gradeLevel-uuid")
            @PathVariable String id) {

        log.debug("GET /api/admin/grade-levels/{} - Obtener curso", id);

        return ResponseEntity.ok(getGradeLevelUseCase.execute(id));
    }

    // ============================================================
    // ASSIGN HOMEROOM TEACHER
    // ============================================================

    @PutMapping("/{id}/homeroom-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Asignar profesor tutor",
            description = "Asigna un profesor como tutor (homeroom teacher) del curso."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tutor asignado correctamente"),
            @ApiResponse(responseCode = "400", description = "ID de profesor inválido"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<GradeLevelResponse> assignHomeroomTeacher(
            @Parameter(description = "ID del curso")
            @PathVariable String id,

            @Valid @RequestBody AssignHomeroomTeacherRequest request) {

        log.info("PUT /api/admin/grade-levels/{}/homeroom-teacher - Asignar tutor {}",
                id, request.teacherId());

        GradeLevelResponse response = assignHomeroomTeacherUseCase.execute(id, request.teacherId());

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // DEACTIVATE GRADE LEVEL (SOFT DELETE)
    // ============================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Desactivar curso",
            description = "Desactiva un curso del sistema (soft delete)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<GradeLevelResponse> deactivateGradeLevel(
            @Parameter(description = "ID del curso")
            @PathVariable String id) {

        log.info("DELETE /api/admin/grade-levels/{} - Desactivar curso", id);

        GradeLevelResponse response = deactivateGradeLevelUseCase.execute(id);

        return ResponseEntity.ok(response);
    }
}
