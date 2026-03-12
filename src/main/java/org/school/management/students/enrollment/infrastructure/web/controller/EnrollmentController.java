package org.school.management.students.enrollment.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.students.enrollment.application.usecases.GetActiveEnrollmentUseCase;
import org.school.management.students.enrollment.application.usecases.GetEnrollmentByStudentIdUseCase;
import org.school.management.students.enrollment.application.usecases.UpdateEnrollmentUseCase;
import org.school.management.students.enrollment.infrastructure.web.dto.EnrollmentWebDto;
import org.school.management.students.enrollment.infrastructure.web.mapper.EnrollmentWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/students/{studentId}/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enrollments", description = "Gestión de matrículas de estudiantes")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

    private final GetEnrollmentByStudentIdUseCase getEnrollmentByStudentIdUseCase;
    private final GetActiveEnrollmentUseCase getActiveEnrollmentUseCase;
    private final UpdateEnrollmentUseCase updateEnrollmentUseCase;
    private final EnrollmentWebMapper mapper;

    // ── GET /api/admin/students/{studentId}/enrollments ───────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener historial de matrículas del estudiante")
    public ResponseEntity<List<EnrollmentWebDto.EnrollmentWebResponse>> getAllEnrollments(
            @PathVariable UUID studentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("GET enrollments — studentId: {}, requestedBy: {}",
                studentId, extractUserId(userDetails));

        List<EnrollmentWebDto.EnrollmentWebResponse> response =
                getEnrollmentByStudentIdUseCase.execute(studentId)
                        .stream()
                        .map(mapper::toWebResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // ── GET /api/admin/students/{studentId}/enrollments/{academicYearId} ──
    @GetMapping("/{academicYearId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Obtener matrícula del estudiante en un año académico")
    public ResponseEntity<EnrollmentWebDto.EnrollmentWebResponse> getEnrollmentByYear(
            @PathVariable UUID studentId,
            @PathVariable UUID academicYearId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("GET enrollment — studentId: {}, academicYearId: {}, requestedBy: {}",
                studentId, academicYearId, extractUserId(userDetails));

        EnrollmentWebDto.EnrollmentWebResponse response = mapper.toWebResponse(
                getActiveEnrollmentUseCase.execute(studentId, academicYearId)
        );

        return ResponseEntity.ok(response);
    }

    // ── PATCH /api/admin/students/{studentId}/enrollments/{enrollmentId} ──
    @PatchMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar matrícula — cierre de ciclo o baja del estudiante")
    public ResponseEntity<EnrollmentWebDto.EnrollmentWebResponse> updateEnrollment(
            @PathVariable UUID studentId,
            @PathVariable UUID enrollmentId,
            @Valid @RequestBody EnrollmentWebDto.UpdateEnrollmentWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("PATCH enrollment — enrollmentId: {}, requestedBy: {}",
                enrollmentId, extractUserId(userDetails));

        EnrollmentWebDto.EnrollmentWebResponse response = mapper.toWebResponse(
                updateEnrollmentUseCase.execute(
                        enrollmentId,
                        mapper.toApplicationRequest(webRequest)
                )
        );

        return ResponseEntity.ok(response);
    }

    // ── Utilidad ──────────────────────────────────────────────────────────
    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }
        throw new IllegalStateException(
                "Principal inesperado: " + userDetails.getClass().getName()
        );
    }
}