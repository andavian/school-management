CREATE TABLE students (
    student_id          BINARY(16) PRIMARY KEY,
    user_id             BINARY(16) NOT NULL UNIQUE,

    -- Datos personales
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    birth_date          DATE NOT NULL,
    birth_place_id      BINARY(16) NOT NULL,
    gender              VARCHAR(10) NOT NULL,  -- MALE, FEMALE, OTHER
    nationality         VARCHAR(100) NOT NULL DEFAULT 'Argentina',

    -- Documentación
    dni_front_image     VARCHAR(500),  -- URL/path imagen frente DNI
    dni_back_image      VARCHAR(500),  -- URL/path imagen dorso DNI
    profile_photo       VARCHAR(500),

    -- Información de contacto
    phone               VARCHAR(20),
    address_street      VARCHAR(200),
    address_number      VARCHAR(10),
    address_floor       VARCHAR(10),
    address_apartment   VARCHAR(10),
    place_id            BINARY(16) NOT NULL,  -- Domicilio actual
    postal_code         VARCHAR(10),

    -- Información médica básica
    blood_type          VARCHAR(5),  -- A+, O-, etc.
    health_insurance    VARCHAR(100),
    health_insurance_number VARCHAR(50),
    allergies           TEXT,
    medical_observations TEXT,

    -- Registro de calificaciones
    registry_id         BINARY(16) NOT NULL,
    folio_number        INT NOT NULL,

    -- Estado del estudiante
    enrollment_status   VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, GRADUATED, WITHDRAWN
    enrollment_date     DATE NOT NULL,
    withdrawal_date     DATE,
    withdrawal_reason   TEXT,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (birth_place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (registry_id) REFERENCES qualification_registries(registry_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    UNIQUE KEY unique_registry_folio (registry_id, folio_number),
    INDEX idx_user_id (user_id),
    INDEX idx_last_name (last_name),
    INDEX idx_dni (user_id),  -- Para búsquedas por DNI a través de user
    INDEX idx_enrollment_status (enrollment_status),
    INDEX idx_registry (registry_id)
);
