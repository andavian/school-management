CREATE TABLE academic_years (
    academic_year_id    BINARY(16) PRIMARY KEY,
    year                INT NOT NULL UNIQUE,  -- 2024, 2025
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    is_current          BOOLEAN NOT NULL DEFAULT FALSE,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, ACTIVE, CLOSED
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_year (year),
    INDEX idx_current (is_current)
);

CREATE TABLE orientations (
    orientation_id      BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,  -- Técnico Electricista, Técnico Electromecánico
    code                VARCHAR(20) NOT NULL UNIQUE,   -- ELEC, ELMEC
    description         TEXT,
    available_from_year INT NOT NULL,  -- 4 (disponible desde 4° año)
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,

    INDEX idx_code (code)
);

CREATE TABLE grade_levels (
    grade_level_id      BINARY(16) PRIMARY KEY,
    academic_year_id    BINARY(16) NOT NULL,
    year_level          INT NOT NULL,  -- 1, 2, 3, 4, 5, 6, 7
    division            VARCHAR(2) NOT NULL,  -- A, B, C, D
    orientation_id      BINARY(16),  -- NULL para 1°-3°, requerido para 4°-7°
    shift               VARCHAR(10) NOT NULL,  -- MORNING, AFTERNOON, EVENING
    max_students        INT NOT NULL DEFAULT 35,
    homeroom_teacher_id BINARY(16),  -- Profesor tutor
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    FOREIGN KEY (orientation_id) REFERENCES orientations(orientation_id) ON DELETE RESTRICT,
    FOREIGN KEY (homeroom_teacher_id) REFERENCES teachers(teacher_id) ON DELETE SET NULL,
    UNIQUE KEY unique_grade_division_year (academic_year_id, year_level, division),
    INDEX idx_academic_year (academic_year_id),
    INDEX idx_year_level (year_level)
);

CREATE TABLE subjects (
    subject_id          BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,  -- Matemática, Física, Programación
    code                VARCHAR(20) NOT NULL UNIQUE,  -- MAT1, FIS2, PROG3
    year_level          INT NOT NULL,  -- En qué año se cursa (1-7)
    orientation_id      BINARY(16),
    is_mandatory        BOOLEAN NOT NULL DEFAULT TRUE,
    weekly_hours        INT NOT NULL,  -- Horas semanales
    description         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (orientation_id) REFERENCES orientations(orientation_id) ON DELETE RESTRICT,
    INDEX idx_year_level (year_level),
    INDEX idx_code (code),
    INDEX idx_orientation (orientation_id)
);


CREATE TABLE qualification_registries (
    registry_id         BINARY(16) PRIMARY KEY,
    registry_number     VARCHAR(20) NOT NULL UNIQUE,  -- REG-2024-001
    academic_year_id    BINARY(16) NOT NULL,
    start_folio         INT NOT NULL,  -- 1
    end_folio           INT NOT NULL,  -- 500
    current_folio       INT NOT NULL DEFAULT 1,
    max_folios          INT NOT NULL DEFAULT 500,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, FULL, CLOSED
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,

    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    INDEX idx_registry_number (registry_number),
    INDEX idx_academic_year (academic_year_id),
    INDEX idx_status (status)
);
