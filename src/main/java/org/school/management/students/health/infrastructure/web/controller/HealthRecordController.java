package org.school.management.students.health.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.health.application.usecases.GetHealthRecordByStudentIdUseCase;
import org.school.management.students.health.application.usecases.UpdateHealthRecordUseCase;
import org.school.management.students.health.infrastructure.web.dto.HealthRecordWebDto;
import org.school.management.students.health.infrastructure.web.mapper.HealthRecordWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoints REST para fichas médicas de estudiantes.
 *
 * Rutas anidadas bajo /students/{studentId}/health para reflejar
 * la relación 1:1 entre estudiante y ficha médica.
 */
@RestController
@RequestMapping("/api/admin/students/{studentId}/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Health Records", description = "Gestión de fichas médicas de estudiantes")
@SecurityRequirement(name = "bearerAuth")
public class HealthRecordController {

    private final GetHealthRecordByStudentIdUseCase getHealthRecordUseCase;
    private final UpdateHealthRecordUseCase updateHealthRecordUseCase;
    private final HealthRecordWebMapper webMapper;

    /**
     * GET /api/admin/students/{studentId}/health
     * Obtiene la ficha médica de un estudiante.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener ficha médica por ID de estudiante")
    public ResponseEntity<HealthRecordWebDto.HealthRecordWebResponse> getByStudentId(
            @PathVariable UUID studentId) {

        var response = getHealthRecordUseCase.execute(studentId);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    /**
     * PATCH /api/admin/students/{studentId}/health
     * Actualiza la ficha médica. Solo los campos enviados reemplazan los existentes.
     */
    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Actualizar ficha médica del estudiante")
    public ResponseEntity<HealthRecordWebDto.HealthRecordWebResponse> update(
            @PathVariable UUID studentId,
            @Valid @RequestBody HealthRecordWebDto.UpdateHealthRecordWebRequest request) {

        var appRequest = webMapper.toApplicationRequest(request);
        var response   = updateHealthRecordUseCase.execute(studentId, appRequest);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }
}