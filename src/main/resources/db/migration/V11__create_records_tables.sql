-- ================================================
-- AGREGADO: StudentRecord (legajo del estudiante)
-- El legajo es ÚNICO POR ESTUDIANTE — no por año.
-- El número de legajo ES el DNI del estudiante (8 dígitos).
-- ================================================
CREATE TABLE document_types (
    document_type_id    BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    code                VARCHAR(20) NOT NULL UNIQUE,
    description         TEXT,
    is_mandatory        BOOLEAN NOT NULL DEFAULT FALSE,
    category            VARCHAR(50) NOT NULL,  -- PERSONAL, ACADEMIC, MEDICAL, LEGAL
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,

    INDEX idx_code (code),
    INDEX idx_category (category)
);

CREATE TABLE student_records (
    record_id           BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL UNIQUE,  -- Un legajo por estudiante
    academic_year_id    BINARY(16) NOT NULL,          -- Año en que se creó el legajo

    record_number       VARCHAR(8) NOT NULL UNIQUE,   -- DNI del estudiante (8 dígitos)
    registry_id         BINARY(16) NOT NULL,
    folio_number        INT NOT NULL,

    status              VARCHAR(20) NOT NULL DEFAULT 'INCOMPLETE',
    completeness_percentage DECIMAL(5,2) DEFAULT 0.00,

    reviewed_by         BINARY(16),
    reviewed_at         TIMESTAMP NULL,
    review_observations TEXT,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES student_personal_data(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    FOREIGN KEY (registry_id) REFERENCES qualification_registries(registry_id) ON DELETE RESTRICT,

    UNIQUE KEY unique_student_record (student_id),       -- Un legajo por estudiante
    UNIQUE KEY unique_record_number (record_number),     -- DNI único como número de legajo
    INDEX idx_status (status),
    INDEX idx_academic_year (academic_year_id)
);

CREATE TABLE record_documents (
    document_id         BINARY(16) PRIMARY KEY,
    record_id           BINARY(16) NOT NULL,
    document_type_id    BINARY(16) NOT NULL,

    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    file_path           VARCHAR(500) NOT NULL,
    file_name           VARCHAR(255) NOT NULL,
    file_size           BIGINT NOT NULL,
    mime_type           VARCHAR(100) NOT NULL,

    issue_date          DATE,
    expiry_date         DATE,
    issuing_authority   VARCHAR(200),

    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    uploaded_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by         BINARY(16) NOT NULL,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (record_id) REFERENCES student_records(record_id) ON DELETE CASCADE,
    FOREIGN KEY (document_type_id) REFERENCES document_types(document_type_id) ON DELETE RESTRICT,
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    INDEX idx_record_id (record_id),
    INDEX idx_document_type (document_type_id),
    INDEX idx_status (status)
);