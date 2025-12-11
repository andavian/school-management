package org.school.management.auth.infra.web.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.usecases.admin.*;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.dto.response.*;
import org.school.management.auth.infra.web.mappers.AuthWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')") // Seguridad global aplicada al módulo
@Tag(
        name = "Admin",
        description = "Gestión administrativa de usuarios (estudiantes, profesores y usuarios del sistema)"
)
public class AdminController {

    private final CreateStudentUseCase createStudentUseCase;
    private final CreateTeacherUseCase createTeacherUseCase;
    private final AuthWebMapper webMapper;

    // ============================================================
    // CREATE STUDENT
    // ============================================================

    @Operation(
            summary = "Crear estudiante",
            description = "Permite crear un nuevo estudiante en el sistema. Solo accesible para administradores."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estudiante creado",
                    content = @Content(schema = @Schema(implementation = CreateStudentApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "El DNI ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/students")
    public ResponseEntity<CreateStudentApiResponse> createStudent(
            @Valid @RequestBody CreateStudentApiRequest request) {

        log.info("POST /api/admin/students - Crear estudiante DNI={} {} {}",
                request.dni(), request.firstName(), request.lastName());

        try {
            var appReq = webMapper.toApplicationDto(request);
            var response = createStudentUseCase.execute(appReq);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(webMapper.toApiResponse(response));

        } catch (DniAlreadyExistsException e) {
            log.warn("Intento de crear estudiante con DNI existente: {}", request.dni());
            throw e;
        }
    }

    // ============================================================
    // CREATE TEACHER
    // ============================================================

    @Operation(
            summary = "Crear profesor",
            description = "Permite crear un nuevo profesor. Solo para administradores."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profesor creado",
                    content = @Content(schema = @Schema(implementation = CreateTeacherApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "El DNI ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/teachers")
    public ResponseEntity<CreateTeacherApiResponse> createTeacher(
            @Valid @RequestBody CreateTeacherApiRequest request) {

        log.info("POST /api/admin/teachers - Crear profesor DNI={} {} {}",
                request.dni(), request.firstName(), request.lastName());

        try {
            var appReq = webMapper.toApplicationDto(request);
            var response = createTeacherUseCase.execute(appReq);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(webMapper.toApiResponse(response));

        } catch (DniAlreadyExistsException e) {
            log.warn("Intento de crear profesor con DNI existente: {}", request.dni());
            throw e;
        }
    }

    // ============================================================
    // GET ALL STUDENTS
    // ============================================================

    @Operation(
            summary = "Listar estudiantes",
            description = "Paginación de estudiantes. Accesible para ADMIN y TEACHER."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/students")
    public ResponseEntity<PagedUsersApiResponse> getAllStudents(
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo de ordenamiento", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección del ordenamiento", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("GET /api/admin/students - page={}, size={}", page, size);

        // TODO: Implementar
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================================
    // GET ALL TEACHERS
    // ============================================================

    @Operation(
            summary = "Listar profesores",
            description = "Lista paginada de profesores. Solo accesible para administradores."
    )
    @GetMapping("/teachers")
    public ResponseEntity<PagedUsersApiResponse> getAllTeachers(
            @Parameter(description = "Página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/admin/teachers - page={}, size={}", page, size);

        // TODO: Implementar
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================================
    // ACTIVATE / DEACTIVATE USER
    // ============================================================

    @Operation(
            summary = "Activar usuario",
            description = "Activa un usuario previamente deshabilitado. Solo para administradores."
    )
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<SuccessApiResponse> activateUser(
            @Parameter(description = "ID del usuario", example = "uuid")
            @PathVariable String userId) {

        log.info("PUT /api/admin/users/{}/activate", userId);

        // TODO: Implementar
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    @Operation(
            summary = "Desactivar usuario",
            description = "Deshabilita un usuario para que no pueda acceder al sistema."
    )
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<SuccessApiResponse> deactivateUser(
            @Parameter(description = "ID del usuario", example = "uuid")
            @PathVariable String userId) {

        log.info("PUT /api/admin/users/{}/deactivate", userId);

        // TODO: Implementar
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================================
    // GET USER BY DNI
    // ============================================================

    @Operation(
            summary = "Buscar usuario por DNI",
            description = "Devuelve la información del usuario correspondiente al DNI especificado."
    )
    @GetMapping("/users/dni/{dni}")
    public ResponseEntity<UserApiResponse> getUserByDni(
            @Parameter(description = "DNI del usuario", example = "39876543")
            @PathVariable String dni) {

        log.info("GET /api/admin/users/dni/{}", dni);

        // TODO: Implementar
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }
}
