package org.school.management.students.personal.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.students.personal.application.usecases.CreateStudentUseCase;
import org.school.management.students.personal.application.usecases.GetStudentByDniUseCase;
import org.school.management.students.personal.application.usecases.GetStudentByIdUseCase;
import org.school.management.students.personal.application.usecases.SearchStudentsUseCase;
import org.school.management.students.personal.application.usecases.UpdateStudentUseCase;
import org.school.management.students.personal.infrastructure.web.dto.StudentWebDto;
import org.school.management.students.personal.infrastructure.web.dto.StudentWebDto.*;
import org.school.management.students.personal.infrastructure.web.mapper.StudentWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller para el agregado StudentPersonalData.
 *
 * Base path: /api/admin/students
 * Seguridad: ADMIN para escritura (POST), ADMIN o STAFF para lectura y actualización (GET, PATCH).
 *
 * El usuario autenticado llega como User (domain model de auth/) porque
 * CustomUserDetailsService.loadUserByUsername() devuelve directamente User,
 * que implementa UserDetails. El cast es seguro en todos los endpoints protegidos.
 */
@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "Gestión de estudiantes — datos personales")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final CreateStudentUseCase   createStudentUseCase;
    private final GetStudentByIdUseCase  getStudentByIdUseCase;
    private final GetStudentByDniUseCase getStudentByDniUseCase;
    private final SearchStudentsUseCase  searchStudentsUseCase;
    private final UpdateStudentUseCase   updateStudentUseCase;
    private final StudentWebMapper       webMapper;

    // ── POST /api/admin/students ──────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear estudiante",
            description = "Crea un estudiante completo en 15 pasos atómicos: " +
                    "datos personales + salud + matrícula + legajo. TODO O NADA."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un estudiante con ese DNI o CUIL"),
            @ApiResponse(responseCode = "422", description = "Error de validación de dominio (ej: CUIL↔DNI no coinciden)"),
            @ApiResponse(responseCode = "500", description = "No hay año académico activo")
    })
    public ResponseEntity<StudentWebResponse> createStudent(
            @Valid @RequestBody CreateStudentWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("POST /api/admin/students — DNI: {}", request.dni());

        UUID createdByUserId = extractUserId(userDetails);
        var appRequest  = webMapper.toApplicationRequest(request);
        var appResponse = createStudentUseCase.execute(appRequest, createdByUserId);
        var webResponse = webMapper.toWebResponse(appResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(webResponse);
    }

    // ── GET /api/admin/students/{id} ──────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener estudiante por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<StudentWebResponse> getStudentById(
            @Parameter(description = "UUID del estudiante") @PathVariable UUID id) {

        log.debug("GET /api/admin/students/{}", id);

        var appResponse = getStudentByIdUseCase.execute(id);
        return ResponseEntity.ok(webMapper.toWebResponse(appResponse));
    }

    // ── GET /api/admin/students/dni/{dni} ─────────────────────────────────

    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener estudiante por DNI")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<StudentWebResponse> getStudentByDni(
            @Parameter(description = "DNI del estudiante (8 dígitos)") @PathVariable String dni) {

        log.debug("GET /api/admin/students/dni/{}", dni);

        var appResponse = getStudentByDniUseCase.execute(dni);
        return ResponseEntity.ok(webMapper.toWebResponse(appResponse));
    }

    // ── GET /api/admin/students ───────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
            summary = "Buscar estudiantes",
            description = "Búsqueda con prioridad: dni > residencePlaceId > fullName > todos. " +
                    "Solo se usa el primer parámetro no nulo."
    )
    public ResponseEntity<StudentWebDto.StudentSearchWebResponse> searchStudents(
            @Parameter(description = "DNI exacto (8 dígitos)")
            @RequestParam(required = false) String dni,

            @Parameter(description = "Nombre o apellido (búsqueda parcial)")
            @RequestParam(required = false) String fullName,

            @Parameter(description = "UUID de la localidad de residencia")
            @RequestParam(required = false) UUID residencePlaceId) {

        log.debug("GET /api/admin/students — dni={}, fullName={}, residencePlaceId={}",
                dni, fullName, residencePlaceId);

        var appSummaries = searchStudentsUseCase.execute(dni, fullName, residencePlaceId);
        var webSummaries = webMapper.toSummaryWebResponseList(appSummaries);

        return ResponseEntity.ok(
                new StudentWebDto.StudentSearchWebResponse(webSummaries, webSummaries.size())
        );
    }

    // ── PATCH /api/admin/students/{id} ────────────────────────────────────

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
            summary = "Actualizar contacto y domicilio",
            description = "Solo actualiza: nombre, teléfono, email y domicilio. " +
                    "DNI, CUIL y datos de nacimiento son inmutables."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante actualizado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
            @ApiResponse(responseCode = "422", description = "Datos inválidos")
    })
    public ResponseEntity<StudentWebResponse> updateStudent(
            @Parameter(description = "UUID del estudiante") @PathVariable UUID id,
            @Valid @RequestBody UpdateStudentWebRequest request) {

        log.info("PATCH /api/admin/students/{}", id);

        var appRequest  = webMapper.toApplicationRequest(request);
        var appResponse = updateStudentUseCase.execute(id, appRequest);
        return ResponseEntity.ok(webMapper.toWebResponse(appResponse));
    }

    // ── Helper privado ────────────────────────────────────────────────────

    /**
     * Extrae el UUID del usuario autenticado.
     *
     * CustomUserDetailsService.loadUserByUsername() devuelve directamente User
     * (domain model de auth/), que implementa UserDetails. El cast es seguro
     * porque Spring Security garantiza que @AuthenticationPrincipal es el objeto
     * devuelto por loadUserByUsername() — que en este proyecto siempre es User.
     *
     * User.getUserId() está disponible via @Data de Lombok → getUserId().value()
     * devuelve el UUID interno del UserId record.
     */
    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }
        throw new IllegalStateException(
                "Principal inesperado en el contexto de seguridad: "
                        + userDetails.getClass().getName()
                        + ". Se esperaba org.school.management.auth.domain.model.User"
        );
    }
}