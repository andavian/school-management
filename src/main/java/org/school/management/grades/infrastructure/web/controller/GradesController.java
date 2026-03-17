package org.school.management.grades.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.grades.application.usecases.CalculateFinalGradeUseCase;
import org.school.management.grades.application.usecases.CalculatePeriodGradeUseCase;
import org.school.management.grades.application.usecases.CreateEvaluationUseCase;
import org.school.management.grades.application.usecases.GradeEvaluationUseCase;
import org.school.management.grades.application.usecases.RecordExamGradeUseCase;
import org.school.management.grades.application.usecases.RecordFinalGradeInRegistryUseCase;
import org.school.management.grades.application.usecases.ValidateEvaluationUseCase;
import org.school.management.grades.infrastructure.web.dto.GradesWebDto;
import org.school.management.grades.infrastructure.web.mapper.GradesWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Grades")
@SecurityRequirement(name = "bearerAuth")
public class GradesController {

    private final CreateEvaluationUseCase createEvaluationUseCase;
    private final GradeEvaluationUseCase gradeEvaluationUseCase;
    private final ValidateEvaluationUseCase validateEvaluationUseCase;
    private final CalculatePeriodGradeUseCase calculatePeriodGradeUseCase;
    private final RecordExamGradeUseCase recordExamGradeUseCase;
    private final CalculateFinalGradeUseCase calculateFinalGradeUseCase;
    private final RecordFinalGradeInRegistryUseCase recordFinalGradeInRegistryUseCase;
    private final GradesWebMapper mapper;

    // ── Evaluaciones ───────────────────────────────────────

    @PostMapping("/evaluations")
    @PreAuthorize("hasAnyRole('TEACHER')")
    @Operation(summary = "Crear evaluación")
    public ResponseEntity<GradesWebDto.EvaluationWebResponse> createEvaluation(
            @Valid @RequestBody GradesWebDto.CreateEvaluationWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toWebResponse(
                        createEvaluationUseCase.execute(
                                mapper.toApplicationRequest(request),
                                extractUserId(userDetails))));
    }

    @PatchMapping("/evaluations/{evaluationId}/grade")
    @PreAuthorize("hasAnyRole('TEACHER')")
    @Operation(summary = "Cargar nota de evaluación")
    public ResponseEntity<GradesWebDto.EvaluationWebResponse> gradeEvaluation(
            @PathVariable UUID evaluationId,
            @Valid @RequestBody GradesWebDto.GradeEvaluationWebRequest request) {

        return ResponseEntity.ok(mapper.toWebResponse(
                gradeEvaluationUseCase.execute(
                        evaluationId,
                        mapper.toApplicationRequest(request))));
    }

    @PatchMapping("/evaluations/{evaluationId}/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Validar evaluación")
    public ResponseEntity<GradesWebDto.EvaluationWebResponse> validateEvaluation(
            @PathVariable UUID evaluationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(mapper.toWebResponse(
                validateEvaluationUseCase.execute(
                        evaluationId,
                        extractUserId(userDetails))));
    }

    // ── Notas de período ───────────────────────────────────

    @PostMapping("/period-grades/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Calcular nota de período")
    public ResponseEntity<GradesWebDto.PeriodGradeWebResponse> calculatePeriodGrade(
            @RequestParam UUID studentCourseSubjectId,
            @RequestParam UUID periodId) {

        return ResponseEntity.ok(mapper.toWebResponse(
                calculatePeriodGradeUseCase.execute(
                        studentCourseSubjectId,
                        periodId)));
    }

    // ── Examen / coloquio ──────────────────────────────────

    @PostMapping("/final-grades/exam")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Asentar nota de examen/coloquio")
    public ResponseEntity<GradesWebDto.FinalGradeWebResponse> recordExamGrade(
            @Valid @RequestBody GradesWebDto.RecordExamGradeWebRequest request) {

        return ResponseEntity.ok(mapper.toWebResponse(
                recordExamGradeUseCase.execute(
                        mapper.toApplicationRequest(request))));
    }

    // ── Nota final ─────────────────────────────────────────

    @PostMapping("/final-grades/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Calcular nota final")
    public ResponseEntity<GradesWebDto.FinalGradeWebResponse> calculateFinalGrade(
            @RequestParam UUID studentCourseSubjectId,
            @RequestParam UUID academicYearId) {

        return ResponseEntity.ok(mapper.toWebResponse(
                calculateFinalGradeUseCase.execute(
                        studentCourseSubjectId,
                        academicYearId)));
    }

    @PatchMapping("/final-grades/{finalGradeId}/registry")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Registrar nota final en libro matriz")
    public ResponseEntity<GradesWebDto.FinalGradeWebResponse> recordFinalGradeInRegistry(
            @PathVariable UUID finalGradeId,
            @Valid @RequestBody GradesWebDto.RecordFinalGradeInRegistryWebRequest request) {

        return ResponseEntity.ok(mapper.toWebResponse(
                recordFinalGradeInRegistryUseCase.execute(
                        finalGradeId,
                        request.studentId())));
    }

    // ── Helper ─────────────────────────────────────────────

    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }
        throw new IllegalStateException(
                "Unexpected principal type: " + userDetails.getClass().getName());
    }
}