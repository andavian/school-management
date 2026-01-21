-- ================================================
-- AGREGADO: StudentPersonalData
-- ================================================
CREATE TABLE student_personal_data (
    student_id          BINARY(16) PRIMARY KEY,
    user_id             BINARY(16) NOT NULL UNIQUE,

    -- Datos personales (del Shared Kernel)
    dni                 VARCHAR(8) NOT NULL UNIQUE,
    cuil                VARCHAR(11) NOT NULL UNIQUE,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    birth_date          DATE NOT NULL,
    birth_place_id      BINARY(16) NOT NULL,
    gender              VARCHAR(10) NOT NULL,
    nationality         VARCHAR(100) NOT NULL,

    -- Contacto
    phone               VARCHAR(20),
    email               VARCHAR(255),
    address_street      VARCHAR(200),
    address_number      VARCHAR(10),
    address_floor       VARCHAR(10),
    address_apartment   VARCHAR(10),
    residence_place_id  BINARY(16) NOT NULL,
    postal_code         VARCHAR(10),

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (birth_place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (residence_place_id) REFERENCES places(place_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    UNIQUE KEY unique_dni (dni),
    UNIQUE KEY unique_cuil (cuil),
    INDEX idx_last_name (last_name),
    INDEX idx_birth_date (birth_date)
);

-- ================================================
-- AGREGADO: StudentHealthRecord
-- ================================================
CREATE TABLE student_health_records (
    health_record_id    BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL UNIQUE, -- 1:1 con student_personal_data

    -- Datos médicos
    blood_type          VARCHAR(5),
    health_insurance    VARCHAR(100),
    health_insurance_number VARCHAR(50),
    allergies           TEXT,
    chronic_conditions  TEXT,
    medications         TEXT,
    medical_observations TEXT,

    -- Contacto de emergencia
    emergency_contact_name VARCHAR(200) NOT NULL,
    emergency_contact_phone VARCHAR(20) NOT NULL,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES student_personal_data(student_id) ON DELETE CASCADE
);
