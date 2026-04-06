package org.school.management.teachingmaterials.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;

import java.util.UUID;

/**
 * Request para subir un material pedagógico.
 * El archivo en sí se recibe como {@code MultipartFile} en el controller — no va aquí.
 */
public record UploadMaterialRequest(

        @NotNull(message = "CourseSubjectId is required")
        UUID courseSubjectId,

        @NotNull(message = "SubjectId is required")
        UUID subjectId,

        @NotNull(message = "AcademicYearId is required")
        UUID academicYearId,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "MaterialType is required")
        MaterialType materialType,

        boolean visibleToStudents
) {}