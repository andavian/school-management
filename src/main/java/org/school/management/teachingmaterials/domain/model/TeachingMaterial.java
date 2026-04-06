package org.school.management.teachingmaterials.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;
import org.school.management.teachingmaterials.domain.valueobject.TeachingMaterialId;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeachingMaterial {

    @EqualsAndHashCode.Include
    private final TeachingMaterialId materialId;

    // Asociaciones — solo IDs, nunca clases de otros BCs
    private final TeacherId teacherId;
    private final CourseSubjectId courseSubjectId;
    private final SubjectId subjectId;           // desnormalizado para búsquedas
    private final AcademicYearId academicYearId; // desnormalizado para búsquedas

    // Metadata del material
    private String title;
    private String description;
    private MaterialType materialType;

    // Almacenamiento OCI
    private final String filePath;  // objectName en OCI (para delete y presigned URLs)
    private final String fileName;  // URL pública en OCI (para acceso directo)
    private final long fileSizeBytes;
    private final String mimeType;

    // Visibilidad
    private boolean visibleToStudents;

    // Auditoría
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Factory method ────────────────────────────────────────────────────

    public static TeachingMaterial create(
            TeacherId teacherId,
            CourseSubjectId courseSubjectId,
            SubjectId subjectId,
            AcademicYearId academicYearId,
            String title,
            String description,
            MaterialType materialType,
            String filePath,
            String fileName,
            long fileSizeBytes,
            String mimeType,
            boolean visibleToStudents) {

        if (teacherId == null)       throw new IllegalArgumentException("TeacherId is required");
        if (courseSubjectId == null) throw new IllegalArgumentException("CourseSubjectId is required");
        if (subjectId == null)       throw new IllegalArgumentException("SubjectId is required");
        if (academicYearId == null)  throw new IllegalArgumentException("AcademicYearId is required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (materialType == null)    throw new IllegalArgumentException("MaterialType is required");
        if (filePath == null || filePath.isBlank()) throw new IllegalArgumentException("FilePath is required");
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("FileName is required");
        if (fileSizeBytes <= 0)      throw new IllegalArgumentException("FileSizeBytes must be positive");
        if (mimeType == null || mimeType.isBlank()) throw new IllegalArgumentException("MimeType is required");

        LocalDateTime now = LocalDateTime.now();
        return TeachingMaterial.builder()
                .materialId(TeachingMaterialId.generate())
                .teacherId(teacherId)
                .courseSubjectId(courseSubjectId)
                .subjectId(subjectId)
                .academicYearId(academicYearId)
                .title(title.trim())
                .description(description)
                .materialType(materialType)
                .filePath(filePath)
                .fileName(fileName)
                .fileSizeBytes(fileSizeBytes)
                .mimeType(mimeType)
                .visibleToStudents(visibleToStudents)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // ── Métodos de negocio ────────────────────────────────────────────────

    /**
     * Actualiza metadata editable del material.
     * No permite cambiar el archivo — para eso se debe eliminar y subir de nuevo.
     */
    public void updateMetadata(String title, String description,
                               MaterialType materialType, boolean visibleToStudents) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        this.title = title.trim();
        this.description = description;
        this.materialType = materialType;
        this.visibleToStudents = visibleToStudents;
        this.updatedAt = LocalDateTime.now();
    }

    public void makeVisible() {
        this.visibleToStudents = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void hide() {
        this.visibleToStudents = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsToTeacher(TeacherId teacherId) {
        return this.teacherId.equals(teacherId);
    }
}