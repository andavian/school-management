package org.school.management.teachingmaterials.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.teachingmaterials.application.usecases.*;
import org.school.management.teachingmaterials.infrastructure.web.dto.TeachingMaterialWebDto;
import org.school.management.teachingmaterials.infrastructure.web.mapper.TeachingMaterialWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teaching Materials")
@SecurityRequirement(name = "bearerAuth")
public class TeachingMaterialController {

    private final UploadTeachingMaterialUseCase uploadUseCase;
    private final GetMaterialsByCourseUseCase   getByCourseUseCase;
    private final GetMaterialsForStudentUseCase getForStudentUseCase;
    private final UpdateMaterialUseCase         updateUseCase;
    private final DeleteMaterialUseCase         deleteUseCase;
    private final TeachingMaterialWebMapper     webMapper;

    // ── POST /api/materials ───────────────────────────────────────────────

    @Operation(summary = "Upload a teaching material (TEACHER)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeachingMaterialWebDto.TeachingMaterialWebResponse> upload(
            @Valid @RequestPart("metadata") TeachingMaterialWebDto.UploadMaterialWebRequest webRequest,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID teacherId = SecurityContextHelper.extractUserId(userDetails);

        var appRequest = webMapper.toUploadRequest(webRequest);
        var response   = uploadUseCase.execute(appRequest, file, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(webMapper.toWebResponse(response));
    }

    // ── GET /api/materials/course/{courseSubjectId} ───────────────────────

    @Operation(summary = "List all materials for a course subject (TEACHER, ADMIN, STAFF)")
    @GetMapping("/course/{courseSubjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STAFF')")
    public ResponseEntity<TeachingMaterialWebDto.TeachingMaterialListWebResponse> getByCourse(
            @PathVariable UUID courseSubjectId) {

        var responses = getByCourseUseCase.execute(courseSubjectId);
        return ResponseEntity.ok(webMapper.toListWebResponse(responses));
    }

    // ── GET /api/materials/my-courses ─────────────────────────────────────

    /**
     * Endpoint para STUDENT: recibe los IDs de sus cursos como query params
     * y retorna solo los materiales visibles.
     *
     * El cliente (frontend) es responsable de conocer sus courseSubjectIds
     * (obtenidos previamente vía GET /api/courses/enrollments/{enrollmentId}/courses).
     */
    @Operation(summary = "List visible materials for student's enrolled courses (STUDENT)")
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TeachingMaterialWebDto.TeachingMaterialListWebResponse> getForStudent(
            @RequestParam List<UUID> courseSubjectIds) {

        var responses = getForStudentUseCase.execute(courseSubjectIds);
        return ResponseEntity.ok(webMapper.toListWebResponse(responses));
    }

    // ── PATCH /api/materials/{materialId} ─────────────────────────────────

    @Operation(summary = "Update material metadata (TEACHER owns it, ADMIN/STAFF any)")
    @PatchMapping("/{materialId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STAFF')")
    public ResponseEntity<TeachingMaterialWebDto.TeachingMaterialWebResponse> update(
            @PathVariable UUID materialId,
            @Valid @RequestBody TeachingMaterialWebDto.UpdateMaterialWebRequest webRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = SecurityContextHelper.extractUserId(userDetails);

        // TEACHER: valida propiedad. ADMIN/STAFF: bypass (null = skip ownership check)
        UUID teacherIdForCheck = isTeacher(userDetails) ? userId : null;

        var appRequest = webMapper.toUpdateRequest(webRequest);
        var response   = updateUseCase.execute(materialId, appRequest, teacherIdForCheck);

        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    // ── DELETE /api/materials/{materialId} ────────────────────────────────

    @Operation(summary = "Delete a material from OCI and DB (TEACHER owns it, ADMIN any)")
    @DeleteMapping("/{materialId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID materialId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = SecurityContextHelper.extractUserId(userDetails);

        // TEACHER: valida propiedad. ADMIN: bypass
        UUID teacherIdForCheck = isTeacher(userDetails) ? userId : null;

        deleteUseCase.execute(materialId, teacherIdForCheck);

        return ResponseEntity.noContent().build();
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private boolean isTeacher(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
    }
}