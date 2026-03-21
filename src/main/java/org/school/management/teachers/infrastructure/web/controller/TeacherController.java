package org.school.management.teachers.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.teachers.application.usecases.CreateTeacherUseCase;
import org.school.management.teachers.application.usecases.GetTeacherByIdUseCase;
import org.school.management.teachers.application.usecases.SearchTeachersUseCase;
import org.school.management.teachers.application.usecases.UpdateTeacherUseCase;
import org.school.management.teachers.infrastructure.web.dto.TeacherWebDto;
import org.school.management.teachers.infrastructure.web.mapper.TeacherWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller para la gestión de profesores.
 *
 * <p>Base path: {@code /api/admin/teachers}</p>
 * <p>Seguridad: ADMIN para escritura (POST), ADMIN o STAFF para lectura y actualización (GET, PATCH).</p>
 *
 * <p>La extracción del usuario autenticado se delega a {@link SecurityContextHelper#extractUserId},
 * que centraliza el cast {@code UserDetails} → {@code User} para todos los controllers del proyecto.</p>
 */
@RestController
@RequestMapping("/api/admin/teachers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teachers", description = "Gestión de profesores")
@SecurityRequirement(name = "bearerAuth")
public class TeacherController {

    private final CreateTeacherUseCase  createTeacherUseCase;
    private final GetTeacherByIdUseCase getTeacherByIdUseCase;
    private final UpdateTeacherUseCase  updateTeacherUseCase;
    private final SearchTeachersUseCase searchTeachersUseCase;
    private final TeacherWebMapper      webMapper;

    // ── POST /api/admin/teachers ──────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear profesor",
            description = "Crea un profesor y su usuario asociado. " +
                    "Se genera una contraseña temporal y se envía invitación por email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profesor creado exitosamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe un profesor con ese DNI o CUIL"),
            @ApiResponse(responseCode = "422", description = "Datos inválidos")
    })
    public ResponseEntity<TeacherWebDto.TeacherWebResponse> createTeacher(
            @Valid @RequestBody TeacherWebDto.CreateTeacherWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("POST /api/admin/teachers — DNI: {}", webRequest.dni());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                webMapper.toWebResponse(
                        createTeacherUseCase.execute(
                                webMapper.toApplicationRequest(webRequest),
                                SecurityContextHelper.extractUserId(userDetails)
                        )
                )
        );
    }

    // ── GET /api/admin/teachers/{teacherId} ───────────────────────────────

    @GetMapping("/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener profesor por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profesor encontrado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public ResponseEntity<TeacherWebDto.TeacherWebResponse> getTeacherById(
            @Parameter(description = "UUID del profesor") @PathVariable UUID teacherId) {

        log.debug("GET /api/admin/teachers/{}", teacherId);

        return ResponseEntity.ok(
                webMapper.toWebResponse(getTeacherByIdUseCase.execute(teacherId))
        );
    }

    // ── GET /api/admin/teachers ───────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
            summary = "Buscar profesores",
            description = "Búsqueda por DNI exacto o apellido parcial. " +
                    "Sin parámetros retorna todos los profesores activos."
    )
    public ResponseEntity<TeacherWebDto.TeacherSearchWebResponse> searchTeachers(
            @Parameter(description = "DNI exacto (8 dígitos)")
            @RequestParam(required = false) String dni,

            @Parameter(description = "Apellido (búsqueda parcial)")
            @RequestParam(required = false) String lastName) {

        log.debug("GET /api/admin/teachers — dni={}, lastName={}", dni, lastName);

        var summaries = searchTeachersUseCase.execute(dni, lastName);
        return ResponseEntity.ok(
                new TeacherWebDto.TeacherSearchWebResponse(
                        webMapper.toSummaryWebResponseList(summaries),
                        summaries.size()
                )
        );
    }

    // ── PATCH /api/admin/teachers/{teacherId} ─────────────────────────────

    @PatchMapping("/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
            summary = "Actualizar profesor",
            description = "PATCH semántico — null conserva el valor existente. " +
                    "Soporta actualización de datos personales, contacto y profesionales."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profesor actualizado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado"),
            @ApiResponse(responseCode = "422", description = "Datos inválidos")
    })
    public ResponseEntity<TeacherWebDto.TeacherWebResponse> updateTeacher(
            @Parameter(description = "UUID del profesor") @PathVariable UUID teacherId,
            @Valid @RequestBody TeacherWebDto.UpdateTeacherWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("PATCH /api/admin/teachers/{} — requestedBy: {}",
                teacherId, SecurityContextHelper.extractUserId(userDetails));

        return ResponseEntity.ok(
                webMapper.toWebResponse(
                        updateTeacherUseCase.execute(
                                teacherId,
                                webMapper.toApplicationRequest(webRequest)
                        )
                )
        );
    }
}