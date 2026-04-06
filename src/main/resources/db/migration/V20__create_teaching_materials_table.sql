-- ============================================================================
-- V22 — teaching_materials
-- Material pedagógico subido por profesores a OCI Object Storage.
-- Asociado a un course_subject (profesor-materia-curso) con campos
-- subject_id y academic_year_id desnormalizados para búsquedas eficientes.
-- ============================================================================

CREATE TABLE teaching_materials (
    material_id             BINARY(16)      PRIMARY KEY,

    -- Asociaciones (solo IDs — sin FK a teachers para evitar acoplamiento entre BCs)
    teacher_id              BINARY(16)      NOT NULL,
    course_subject_id       BINARY(16)      NOT NULL,
    subject_id              BINARY(16)      NOT NULL,        -- desnormalizado para búsquedas
    academic_year_id        BINARY(16)      NOT NULL,        -- desnormalizado para búsquedas

    -- Metadata del material
    title                   VARCHAR(200)    NOT NULL,
    description             TEXT,
    material_type           VARCHAR(20)     NOT NULL,

    -- Almacenamiento OCI
    file_path               VARCHAR(500)    NOT NULL,        -- objectName en OCI (delete/presigned)
    file_name               VARCHAR(255)    NOT NULL,        -- URL pública en OCI (acceso directo)
    file_size_bytes         BIGINT          NOT NULL,
    mime_type               VARCHAR(100)    NOT NULL,

    -- Visibilidad
    is_visible_to_students  BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Auditoría
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_tm_material_type CHECK (
        material_type IN ('APUNTE', 'EJERCICIO', 'EXAMEN', 'GUIA', 'VIDEO', 'OTRO')
    ),
    CONSTRAINT chk_tm_mime_type CHECK (
        mime_type IN ('application/pdf', 'image/jpeg', 'image/png')
    ),
    CONSTRAINT chk_tm_file_size CHECK (file_size_bytes > 0),

    -- FK solo a tablas del mismo schema que no pertenecen a otro BC de forma acoplada
    CONSTRAINT fk_tm_course_subject
        FOREIGN KEY (course_subject_id) REFERENCES course_subjects(course_subject_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_tm_academic_year
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id)
        ON DELETE RESTRICT,

    -- Índices
    INDEX idx_tm_course_subject     (course_subject_id),
    INDEX idx_tm_teacher            (teacher_id),
    INDEX idx_tm_subject_year       (subject_id, academic_year_id),
    INDEX idx_tm_visible            (is_visible_to_students),
    INDEX idx_tm_teacher_course     (teacher_id, course_subject_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;