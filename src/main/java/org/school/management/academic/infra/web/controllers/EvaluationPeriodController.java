package org.school.management.academic.infra.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.usecases.period.*;
import org.school.management.academic.infra.web.dto.EvaluationPeriodWebDto.CreateEvaluationPeriodWebRequest;
import org.school.management.academic.infra.web.dto.EvaluationPeriodWebDto.EvaluationPeriodWebResponse;
import org.school.management.academic.infra.web.mappers.EvaluationPeriodWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Evaluation Periods", description = "Gestión de períodos de evaluación del sistema académico")
@SecurityRequirement(name = "bearerAuth")
public class EvaluationPeriodController {

    private final CreateEvaluationPeriodUseCase createEvaluationPeriodUseCase;
    private final GetEvaluationPeriodUseCase getEvaluationPeriodUseCase;
    private final ListEvaluationPeriodsUseCase listEvaluationPeriodsUseCase;
    private final ActivateEvaluationPeriodUseCase activateEvaluationPeriodUseCase;
    private final CloseEvaluationPeriodUseCase closeEvaluationPeriodUseCase;
    private final GetCurrentEvaluationPeriodUseCase getCurrentEvaluationPeriodUseCase;
    private final EvaluationPeriodWebMapper webMapper;

    // ============================================================
    // CREATE PERIOD
    // ============================================================
    @PostMapping("/api/admin/academic-years/{academicYearId}/periods")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear período de evaluación", description = "Crea un nuevo período de evaluación vinculado a un ciclo lectivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Período creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "409", description = "El período ya existe para ese año")
    })
    public ResponseEntity<EvaluationPeriodWebResponse> createPeriod(
            @Parameter(description = "ID del ciclo lectivo", example = "academicYear-uuid")
            @PathVariable String academicYearId,
            @Valid @RequestBody CreateEvaluationPeriodWebRequest webRequest) {

        log.info("POST /api/admin/academic-years/{}/periods - Crear período", academicYearId);

        var request = webMapper.toApplicationRequest(webRequest);
        var response = createEvaluationPeriodUseCase.execute(academicYearId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toWebResponse(response));
    }

    // ============================================================
    // LIST PERIODS BY ACADEMIC YEAR
    // ============================================================
    @GetMapping("/api/admin/academic-years/{academicYearId}/periods")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @Operation(summary = "Listar períodos de evaluación", description = "Devuelve todos los períodos de evaluación de un ciclo lectivo.")
    @ApiResponse(responseCode = "200", description = "Períodos obtenidos exitosamente")
    public ResponseEntity<List<EvaluationPeriodWebResponse>> listPeriods(
            @Parameter(description = "ID del ciclo lectivo", example = "academicYear-uuid")
            @PathVariable String academicYearId) {

        log.debug("GET /api/admin/academic-years/{}/periods - Listar períodos", academicYearId);

        List<EvaluationPeriodWebResponse> responses = listEvaluationPeriodsUseCase.execute(academicYearId)
                .stream()
                .map(webMapper::toWebResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ============================================================
    // GET CURRENT PERIOD
    // ============================================================
    @GetMapping("/api/admin/academic-years/{academicYearId}/periods/current")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','TEACHER')")
    @Operation(summary = "Obtener período actual", description = "Devuelve el período de evaluación marcado como activo para un ciclo lectivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Período actual encontrado"),
            @ApiResponse(responseCode = "404", description = "Sin período activo o ciclo lectivo no encontrado")
    })
    public ResponseEntity<EvaluationPeriodWebResponse> getCurrentPeriod(
            @Parameter(description = "ID del ciclo lectivo", example = "academicYear-uuid")
            @PathVariable String academicYearId) {

        log.debug("GET /api/admin/academic-years/{}/periods/current - Período actual", academicYearId);

        var response = getCurrentEvaluationPeriodUseCase.execute(academicYearId);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    // ============================================================
    // GET PERIOD BY ID
    // ============================================================
    @GetMapping("/api/admin/periods/{periodId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @Operation(summary = "Obtener período por ID", description = "Devuelve los detalles de un período de evaluación específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Período encontrado"),
            @ApiResponse(responseCode = "404", description = "Período no encontrado")
    })
    public ResponseEntity<EvaluationPeriodWebResponse> getPeriod(
            @Parameter(description = "ID del período", example = "period-uuid")
            @PathVariable String periodId) {

        log.debug("GET /api/admin/periods/{} - Obtener período", periodId);

        var response = getEvaluationPeriodUseCase.execute(periodId);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    // ============================================================
    // ACTIVATE PERIOD
    // ============================================================
    @PatchMapping("/api/admin/periods/{periodId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar período", description = "Marca un período de evaluación como activo y actual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Período activado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El período ya está cerrado"),
            @ApiResponse(responseCode = "404", description = "Período no encontrado")
    })
    public ResponseEntity<EvaluationPeriodWebResponse> activatePeriod(
            @Parameter(description = "ID del período", example = "period-uuid")
            @PathVariable String periodId) {

        log.info("PATCH /api/admin/periods/{}/activate - Activar período", periodId);

        var response = activateEvaluationPeriodUseCase.execute(periodId);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    // ============================================================
    // CLOSE PERIOD
    // ============================================================
    @PatchMapping("/api/admin/periods/{periodId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cerrar período", description = "Cierra un período de evaluación, impidiendo nuevas calificaciones.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Período cerrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Período no encontrado")
    })
    public ResponseEntity<EvaluationPeriodWebResponse> closePeriod(
            @Parameter(description = "ID del período", example = "period-uuid")
            @PathVariable String periodId) {

        log.info("PATCH /api/admin/periods/{}/close - Cerrar período", periodId);

        var response = closeEvaluationPeriodUseCase.execute(periodId);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }
}