package org.school.management.teachingmaterials.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.storage.domain.model.UploadedFile;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.application.dto.request.UploadMaterialRequest;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.application.mapper.TeachingMaterialApplicationMapper;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Caso de uso: subir un material pedagógico a OCI y persistirlo.
 *
 * <p>Flujo:</p>
 * <ol>
 *   <li>Valida tipo MIME y tamaño del archivo</li>
 *   <li>Sube el archivo a OCI Object Storage via {@link StorageService}</li>
 *   <li>Construye y persiste la entidad {@link TeachingMaterial}</li>
 * </ol>
 *
 * <p>Si la subida a OCI falla, la transacción no llega a persistir nada
 * — no quedan registros huérfanos en BD.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UploadTeachingMaterialUseCase {

    private static final int  MAX_SIZE_MB    = 10;
    private static final long MAX_SIZE_BYTES = (long) MAX_SIZE_MB * 1024 * 1024;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final TeachingMaterialRepository materialRepository;
    private final StorageService storageService;
    private final TeachingMaterialApplicationMapper mapper;

    public TeachingMaterialResponse execute(UploadMaterialRequest request,
                                            MultipartFile file,
                                            UUID teacherIdRaw) {
        log.info("Uploading teaching material — courseSubjectId: {}, teacher: {}",
                request.courseSubjectId(), teacherIdRaw);

        // ── 1. Validar archivo ────────────────────────────────────────────
        validateFile(file);

        // ── 2. Subir a OCI ────────────────────────────────────────────────
        // Carpeta: materials/{teacherId}/{courseSubjectId}/
        String folder = "materials/" + teacherIdRaw + "/" + request.courseSubjectId();

        UploadedFile uploaded;
        try {
            uploaded = storageService.upload(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    folder
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error reading file for upload: " + e.getMessage(), e);
        }

        log.info("File uploaded to OCI — object: {}", uploaded.objectName());

        // ── 3. Construir y persistir TeachingMaterial ─────────────────────
        TeachingMaterial material = TeachingMaterial.create(
                TeacherId.from(teacherIdRaw),
                CourseSubjectId.of(request.courseSubjectId()),
                SubjectId.of(request.subjectId()),
                AcademicYearId.of(request.academicYearId()),
                request.title(),
                request.description(),
                request.materialType(),
                uploaded.objectName(),  // filePath = objectName para delete/presigned
                uploaded.publicUrl(),   // fileName = URL pública para acceso directo
                uploaded.sizeBytes(),
                uploaded.mimeType(),
                request.visibleToStudents()
        );

        TeachingMaterial saved = materialRepository.save(material);

        log.info("TeachingMaterial persisted — materialId: {}", saved.getMaterialId().value());

        return mapper.toResponse(saved);
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "File type not allowed: " + file.getContentType()
                            + ". Only PDF, JPG and PNG are accepted.");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "File exceeds maximum size of " + MAX_SIZE_MB + " MB");
        }
    }
}