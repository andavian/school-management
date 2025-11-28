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


-- ============================================================================
-- STUDY PLANS (Planes de Estudio)
-- ============================================================================

CREATE TABLE study_plans (
    study_plan_id       BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    code                VARCHAR(20) NOT NULL UNIQUE,
    description         TEXT,
    orientation_id      BINARY(16),
    year_level          INT NOT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_study_plans_orientation
        FOREIGN KEY (orientation_id) REFERENCES orientations(orientation_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_sp_year_level CHECK (year_level BETWEEN 1 AND 7),

    INDEX idx_study_plans_code (code),
    INDEX idx_study_plans_orientation (orientation_id),
    INDEX idx_study_plans_year (year_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Study Plan Subjects (Materias del Plan de Estudios)
CREATE TABLE study_plan_subjects (
    study_plan_subject_id BINARY(16) PRIMARY KEY,
    study_plan_id       BINARY(16) NOT NULL,
    subject_id          BINARY(16) NOT NULL,
    is_mandatory        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sps_study_plan
        FOREIGN KEY (study_plan_id) REFERENCES study_plans(study_plan_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sps_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
        ON DELETE RESTRICT,

    UNIQUE KEY uk_study_plan_subject (study_plan_id, subject_id),

    INDEX idx_sps_study_plan (study_plan_id),
    INDEX idx_sps_subject (subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- EVALUATION PERIODS (Períodos de Evaluación)
-- ============================================================================

CREATE TABLE evaluation_periods (
    period_id           BINARY(16) PRIMARY KEY,
    academic_year_id    BINARY(16) NOT NULL,
    period_number       INT NOT NULL,
    name                VARCHAR(50) NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    is_current          BOOLEAN NOT NULL DEFAULT FALSE,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,

    CONSTRAINT fk_periods_academic_year
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_period_number CHECK (period_number BETWEEN 1 AND 4),
    CONSTRAINT chk_period_dates CHECK (end_date > start_date),
    CONSTRAINT chk_period_status CHECK (status IN ('PENDING', 'ACTIVE', 'CLOSED')),

    UNIQUE KEY uk_period_number_year (academic_year_id, period_number),

    INDEX idx_periods_academic_year (academic_year_id),
    INDEX idx_periods_current (is_current),
    INDEX idx_periods_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



