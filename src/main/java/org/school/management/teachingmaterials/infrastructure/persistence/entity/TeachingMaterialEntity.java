package org.school.management.teachingmaterials.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teaching_materials")
public class TeachingMaterialEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "material_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID materialId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "teacher_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID teacherId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "course_subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID courseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID subjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID academicYearId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 20)
    private MaterialType materialType;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;   // objectName en OCI

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;   // URL pública en OCI

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "is_visible_to_students", nullable = false)
    private boolean visibleToStudents = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}