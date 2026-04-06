package org.school.management.teachingmaterials.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Clase contenedora de todos los Web DTOs del BC teaching-materials.
 * Patrón establecido en TeacherWebDto, CourseWebDto, etc.
 */
public final class TeachingMaterialWebDto {

    private TeachingMaterialWebDto() {}

    // ── Requests ──────────────────────────────────────────────────────────

    /**
     * Metadata del material enviada junto al archivo (multipart/form-data).
     * El archivo va como parte separada del multipart — no aquí.
     */
    public record UploadMaterialWebRequest(

            @NotNull(message = "courseSubjectId is required")
            UUID courseSubjectId,

            @NotNull(message = "subjectId is required")
            UUID subjectId,

            @NotNull(message = "academicYearId is required")
            UUID academicYearId,

            @NotBlank(message = "title is required")
            @Size(max = 200, message = "title cannot exceed 200 characters")
            String title,

            @Size(max = 1000, message = "description cannot exceed 1000 characters")
            String description,

            @NotNull(message = "materialType is required")
            MaterialType materialType,

            boolean visibleToStudents
    ) {}

    /**
     * PATCH semántico — todos los campos son opcionales.
     */
    public record UpdateMaterialWebRequest(
            @Size(max = 200) String title,
            @Size(max = 1000) String description,
            MaterialType materialType,
            Boolean visibleToStudents
    ) {}

    // ── Responses ─────────────────────────────────────────────────────────

    public record TeachingMaterialWebResponse(
            UUID materialId,
            UUID teacherId,
            UUID courseSubjectId,
            UUID subjectId,
            UUID academicYearId,
            String title,
            String description,
            MaterialType materialType,
            String publicUrl,
            long fileSizeBytes,
            String mimeType,
            boolean visibleToStudents,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record TeachingMaterialListWebResponse(
            List<TeachingMaterialWebResponse> materials,
            int total
    ) {}
}