package org.school.management.teachingmaterials.application.dto.response;

import org.school.management.teachingmaterials.domain.valueobject.MaterialType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeachingMaterialResponse(

        UUID materialId,
        UUID teacherId,
        UUID courseSubjectId,
        UUID subjectId,
        UUID academicYearId,

        String title,
        String description,
        MaterialType materialType,

        // Acceso al archivo
        String publicUrl,       // fileName del dominio — URL pública OCI
        long fileSizeBytes,
        String mimeType,

        boolean visibleToStudents,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}