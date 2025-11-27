CREATE TABLE parents (
    parent_id           BINARY(16) PRIMARY KEY,
    user_id             BINARY(16) NOT NULL UNIQUE,

    -- Datos personales
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    dni                 VARCHAR(8) NOT NULL UNIQUE,
    birth_date          DATE,
    gender              VARCHAR(10),
    nationality         VARCHAR(100) DEFAULT 'Argentina',

    -- Información de contacto
    email               VARCHAR(254) NOT NULL UNIQUE,  -- Email es obligatorio para padres
    phone               VARCHAR(20) NOT NULL,
    phone_alt           VARCHAR(20),
    address_street      VARCHAR(200),
    address_number      VARCHAR(10),
    address_floor       VARCHAR(10),
    address_apartment   VARCHAR(10),
    place_id            BINARY(16),
    postal_code         VARCHAR(10),

    -- Información laboral
    occupation          VARCHAR(100),
    workplace           VARCHAR(200),
    workplace_phone     VARCHAR(20),

    -- Estado
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    INDEX idx_user_id (user_id),
    INDEX idx_dni (dni),
    INDEX idx_email (email),
    INDEX idx_last_name (last_name)
);

CREATE TABLE student_parents (
    student_parent_id   BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL,
    parent_id           BINARY(16) NOT NULL,
    relationship        VARCHAR(20) NOT NULL,  -- FATHER, MOTHER, GUARDIAN, OTHER
    is_primary_contact  BOOLEAN NOT NULL DEFAULT FALSE,
    is_authorized_pickup BOOLEAN NOT NULL DEFAULT TRUE,
    is_emergency_contact BOOLEAN NOT NULL DEFAULT TRUE,
    notes               TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES parents(parent_id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_parent (student_id, parent_id),
    INDEX idx_student_id (student_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_primary_contact (is_primary_contact)
);