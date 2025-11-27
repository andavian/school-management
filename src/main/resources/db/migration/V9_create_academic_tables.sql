-- ============================================================================
-- ACADEMIC MODULE - Database Migration
-- File: src/main/resources/db/migration/V6__create_academic_tables.sql
-- ============================================================================

-- Academic Years table
CREATE TABLE academic_years (
    academic_year_id    BINARY(16) PRIMARY KEY,
    year                INT NOT NULL UNIQUE,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    is_current          BOOLEAN NOT NULL DEFAULT FALSE,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_year_range CHECK (year BETWEEN 2000 AND 2100),
    CONSTRAINT chk_dates CHECK (end_date > start_date),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ACTIVE', 'CLOSED')),
    
    INDEX idx_academic_years_year (year),
    INDEX idx_academic_years_current (is_current),
    INDEX idx_academic_years_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Orientations table
CREATE TABLE orientations (
    orientation_id      BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    code                VARCHAR(20) NOT NULL UNIQUE,
    description         TEXT,
    available_from_year INT NOT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_available_year CHECK (available_from_year BETWEEN 1 AND 7),
    
    INDEX idx_orientations_code (code),
    INDEX idx_orientations_active (is_active),
    INDEX idx_orientations_available_year (available_from_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Qualification Registries table (Libro de Calificaciones - Legal)
CREATE TABLE qualification_registries (
    registry_id         BINARY(16) PRIMARY KEY,
    registry_number     VARCHAR(20) NOT NULL UNIQUE,
    academic_year_id    BINARY(16) NOT NULL,
    start_folio         INT NOT NULL,
    end_folio           INT NOT NULL,
    current_folio       INT NOT NULL DEFAULT 1,
    max_folios          INT NOT NULL DEFAULT 500,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,
    
    CONSTRAINT fk_registries_academic_year 
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT chk_folios CHECK (start_folio > 0 AND end_folio >= start_folio),
    CONSTRAINT chk_current_folio CHECK (current_folio BETWEEN start_folio AND end_folio + 1),
    CONSTRAINT chk_registry_status CHECK (status IN ('ACTIVE', 'FULL', 'CLOSED')),
    
    INDEX idx_registries_number (registry_number),
    INDEX idx_registries_academic_year (academic_year_id),
    INDEX idx_registries_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Grade Levels table (Cursos: 1°A, 2°B, etc.)
CREATE TABLE grade_levels (
    grade_level_id      BINARY(16) PRIMARY KEY,
    academic_year_id    BINARY(16) NOT NULL,
    year_level          INT NOT NULL,
    division            VARCHAR(2) NOT NULL,
    orientation_id      BINARY(16),
    shift               VARCHAR(10) NOT NULL,
    max_students        INT NOT NULL DEFAULT 35,
    homeroom_teacher_id BINARY(16),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_grade_levels_academic_year 
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT fk_grade_levels_orientation 
        FOREIGN KEY (orientation_id) REFERENCES orientations(orientation_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT chk_year_level CHECK (year_level BETWEEN 1 AND 7),
    CONSTRAINT chk_division CHECK (division REGEXP '^[A-Z]{1,2}$'),
    CONSTRAINT chk_shift CHECK (shift IN ('MORNING', 'AFTERNOON', 'EVENING')),
    CONSTRAINT chk_max_students CHECK (max_students > 0 AND max_students <= 50),
    
    -- Orientación obligatoria para años 4-7 en escuelas técnicas
    CONSTRAINT chk_orientation_required 
        CHECK (
            (year_level <= 3 AND orientation_id IS NULL) OR 
            (year_level >= 4 AND orientation_id IS NOT NULL) OR
            (year_level >= 4 AND orientation_id IS NULL)  -- Permitir NULL temporalmente
        ),
    
    UNIQUE KEY uk_grade_level_division_year (academic_year_id, year_level, division),
    
    INDEX idx_grade_levels_academic_year (academic_year_id),
    INDEX idx_grade_levels_year (year_level),
    INDEX idx_grade_levels_orientation (orientation_id),
    INDEX idx_grade_levels_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Subjects table (Materias del plan de estudios)
CREATE TABLE subjects (
    subject_id          BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    code                VARCHAR(20) NOT NULL UNIQUE,
    year_level          INT NOT NULL,
    orientation_id      BINARY(16),
    is_mandatory        BOOLEAN NOT NULL DEFAULT TRUE,
    weekly_hours        INT NOT NULL,
    description         TEXT,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_subjects_orientation 
        FOREIGN KEY (orientation_id) REFERENCES orientations(orientation_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT chk_subject_year_level CHECK (year_level BETWEEN 1 AND 7),
    CONSTRAINT chk_weekly_hours CHECK (weekly_hours > 0 AND weekly_hours <= 20),
    
    INDEX idx_subjects_code (code),
    INDEX idx_subjects_year_level (year_level),
    INDEX idx_subjects_orientation (orientation_id),
    INDEX idx_subjects_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Courses table (Asignación profesor-materia-curso)
CREATE TABLE courses (
    course_id           BINARY(16) PRIMARY KEY,
    subject_id          BINARY(16) NOT NULL,
    grade_level_id      BINARY(16) NOT NULL,
    teacher_id          BINARY(16),
    academic_year_id    BINARY(16) NOT NULL,
    schedule_info       JSON,
    classroom           VARCHAR(50),
    max_students        INT DEFAULT 35,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_courses_subject 
        FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT fk_courses_grade_level 
        FOREIGN KEY (grade_level_id) REFERENCES grade_levels(grade_level_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT fk_courses_academic_year 
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) 
        ON DELETE RESTRICT,
    
    CONSTRAINT chk_course_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED')),
    
    UNIQUE KEY uk_course_subject_grade_year (subject_id, grade_level_id, academic_year_id),
    
    INDEX idx_courses_subject (subject_id),
    INDEX idx_courses_grade_level (grade_level_id),
    INDEX idx_courses_teacher (teacher_id),
    INDEX idx_courses_academic_year (academic_year_id),
    INDEX idx_courses_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- INITIAL DATA - Academic Year 2024
-- ============================================================================

-- Insert current academic year (2024)
INSERT INTO academic_years (academic_year_id, year, start_date, end_date, is_current, status, created_at) 
VALUES (
    UNHEX(REPLACE(UUID(), '-', '')),
    2024,
    '2024-03-01',
    '2024-12-15',
    TRUE,
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- Insert academic year 2025
INSERT INTO academic_years (academic_year_id, year, start_date, end_date, is_current, status, created_at) 
VALUES (
    UNHEX(REPLACE(UUID(), '-', '')),
    2025,
    '2025-03-01',
    '2025-12-15',
    FALSE,
    'PENDING',
    CURRENT_TIMESTAMP
);

-- Insert orientations
SET @elec_id = UNHEX(REPLACE(UUID(), '-', ''));
SET @elmec_id = UNHEX(REPLACE(UUID(), '-', ''));

INSERT INTO orientations (orientation_id, name, code, description, available_from_year, is_active, created_at) VALUES
(@elec_id, 'Técnico Electricista', 'ELEC', 'Orientación en instalaciones eléctricas industriales y domiciliarias', 4, TRUE, CURRENT_TIMESTAMP),
(@elmec_id, 'Técnico Electromecánico', 'ELMEC', 'Orientación en sistemas electromecánicos y mantenimiento industrial', 4, TRUE, CURRENT_TIMESTAMP);

-- Insert qualification registry for 2024
SET @year_2024_id = (SELECT academic_year_id FROM academic_years WHERE year = 2024);

INSERT INTO qualification_registries (
    registry_id, 
    registry_number, 
    academic_year_id, 
    start_folio, 
    end_folio, 
    current_folio, 
    max_folios, 
    status, 
    created_at
) VALUES (
    UNHEX(REPLACE(UUID(), '-', '')),
    'REG-2024-001',
    @year_2024_id,
    1,
    500,
    1,
    500,
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- Insert common subjects for all years (1° to 7°)
-- Year 1
INSERT INTO subjects (subject_id, name, code, year_level, orientation_id, is_mandatory, weekly_hours, is_active, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Matemática I', 'MAT1', 1, NULL, TRUE, 5, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Lengua y Literatura I', 'LEN1', 1, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Física I', 'FIS1', 1, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Química I', 'QUI1', 1, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Educación Física I', 'EF1', 1, NULL, TRUE, 2, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Inglés I', 'ING1', 1, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Tecnología I', 'TEC1', 1, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP);

-- Year 2
INSERT INTO subjects (subject_id, name, code, year_level, orientation_id, is_mandatory, weekly_hours, is_active, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Matemática II', 'MAT2', 2, NULL, TRUE, 5, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Lengua y Literatura II', 'LEN2', 2, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Física II', 'FIS2', 2, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Química II', 'QUI2', 2, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Educación Física II', 'EF2', 2, NULL, TRUE, 2, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Inglés II', 'ING2', 2, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Tecnología II', 'TEC2', 2, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP);

-- Year 3
INSERT INTO subjects (subject_id, name, code, year_level, orientation_id, is_mandatory, weekly_hours, is_active, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Matemática III', 'MAT3', 3, NULL, TRUE, 5, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Lengua y Literatura III', 'LEN3', 3, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Física III', 'FIS3', 3, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Química III', 'QUI3', 3, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Educación Física III', 'EF3', 3, NULL, TRUE, 2, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Inglés III', 'ING3', 3, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Tecnología III', 'TEC3', 3, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP);

-- Year 4 - Common subjects + Orientation-specific
INSERT INTO subjects (subject_id, name, code, year_level, orientation_id, is_mandatory, weekly_hours, is_active, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Matemática IV', 'MAT4', 4, NULL, TRUE, 4, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Lengua y Literatura IV', 'LEN4', 4, NULL, TRUE, 3, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Inglés IV', 'ING4', 4, NULL, TRUE, 2, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Educación Física IV', 'EF4', 4, NULL, TRUE, 2, TRUE, CURRENT_TIMESTAMP);

-- Year 4 - Electricista
INSERT INTO subjects (subject_id, name, code, year_level, orientation_id, is_mandatory, weekly_hours, is_active, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Instalaciones Eléctricas I', 'ELEC4-1', 4, @elec_id, TRUE, 6, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Circuitos Eléctricos I', 'ELEC4-2', 4, @elec_id, TRUE, 5, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Máquinas Eléctricas I', 'ELEC4-3', 4, @elec_id, TRUE, 4, TRUE, CURRENT_TIMESTAMP);

-- Year 4 - Electromecánico
INSERT INTO subjects (subject_id, name, code, year_level, orientation_id, is_mandatory, weekly_hours, is_active, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Mecánica Técnica I', 'ELMEC4-1', 4, @elmec_id, TRUE, 6, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Sistemas Electromecánicos I', 'ELMEC4-2', 4, @elmec_id, TRUE, 5, TRUE, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), 'Automatización I', 'ELMEC4-3', 4, @elmec_id, TRUE, 4, TRUE, CURRENT_TIMESTAMP);

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- SELECT 
--     (SELECT COUNT(*) FROM academic_years) as academic_years_count,
--     (SELECT COUNT(*) FROM orientations) as orientations_count,
--     (SELECT COUNT(*) FROM qualification_registries) as registries_count,
--     (SELECT COUNT(*) FROM subjects) as subjects_count;