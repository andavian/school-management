CREATE TABLE teachers (
    teacher_id          BINARY(16) PRIMARY KEY,
    user_id             BINARY(16) NOT NULL UNIQUE,

    -- Datos personales
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(254) NOT NULL UNIQUE,
    birth_date          DATE,
    birth_place_id      BINARY(16),
    gender              VARCHAR(10),
    nationality         VARCHAR(100) DEFAULT 'Argentina',

    -- Información de contacto
    phone               VARCHAR(20) NOT NULL,
    address_street      VARCHAR(200),
    address_number      VARCHAR(10),
    address_floor       VARCHAR(10),
    address_apartment   VARCHAR(10),
    place_id            BINARY(16),
    postal_code         VARCHAR(10),

    -- Información profesional
    specialization      VARCHAR(200),  -- Matemática, Física, etc.
    teaching_license    VARCHAR(100),
    hire_date           DATE NOT NULL,
    employment_status   VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, RETIRED
    employment_type     VARCHAR(20) NOT NULL,  -- FULL_TIME, PART_TIME, CONTRACT

    -- Estado
    is_active           BOOLEAN NOT NULL DEFAULT FALSE,  -- Debe activar cuenta
    activation_token    VARCHAR(64),
    activation_sent_at  TIMESTAMP,
    activated_at        TIMESTAMP,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (birth_place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    INDEX idx_last_name (last_name),
    INDEX idx_employment_status (employment_status)
);
