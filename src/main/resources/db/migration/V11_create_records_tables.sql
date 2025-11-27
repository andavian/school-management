CREATE TABLE student_records (
    record_id           BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL UNIQUE,
    record_number       VARCHAR(20) NOT NULL UNIQUE,  -- LEG-2024-001234
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_record_number (record_number)
);

CREATE TABLE document_types (
    document_type_id    BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,  -- Certificado de Nacimiento, Vacunas
    code                VARCHAR(20) NOT NULL UNIQUE,   -- CERT_NAC, VAC
    description         TEXT,
    is_mandatory        BOOLEAN NOT NULL DEFAULT FALSE,
    category            VARCHAR(50) NOT NULL,  -- PERSONAL, ACADEMIC, MEDICAL, LEGAL
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,

    INDEX idx_code (code),
    INDEX idx_category (category)
);

CREATE TABLE record_documents (
    document_id         BINARY(16) PRIMARY KEY,
    record_id           BINARY(16) NOT NULL,
    document_type_id    BINARY(16) NOT NULL,

    -- Información del documento
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    file_path           VARCHAR(500) NOT NULL,  -- Ruta del archivo en storage
    file_name           VARCHAR(255) NOT NULL,  -- Nombre original
    file_size           BIGINT NOT NULL,  -- Tamaño en bytes
    mime_type           VARCHAR(100) NOT NULL,  -- application/pdf, image/jpeg

    -- Metadata
    issue_date          DATE,
    expiry_date         DATE,
    issuing_authority   VARCHAR(200),

    -- Auditoría
    uploaded_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by         BINARY(16) NOT NULL,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (record_id) REFERENCES student_records(record_id) ON DELETE CASCADE,
    FOREIGN KEY (document_type_id) REFERENCES document_types(document_type_id) ON DELETE RESTRICT,
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    INDEX idx_record_id (record_id),
    INDEX idx_document_type (document_type_id),
    INDEX idx_uploaded_at (uploaded_at)
);
