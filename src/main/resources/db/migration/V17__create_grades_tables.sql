-- ============================================================================
-- EVALUATION TYPES (Tipos de Evaluación)
-- ============================================================================

CREATE TABLE evaluation_types (
    evaluation_type_id  BINARY(16) PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    code                VARCHAR(20) NOT NULL UNIQUE,
    description         TEXT,
    weight_percentage   DECIMAL(5,2) DEFAULT 100.00,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_weight CHECK (weight_percentage BETWEEN 0 AND 100),

    INDEX idx_evaluation_types_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- EVALUATIONS (Evaluaciones Parciales)
-- ============================================================================

CREATE TABLE evaluations (
    evaluation_id       BINARY(16) PRIMARY KEY,
    student_course_subject_id BINARY(16) NOT NULL,
    period_id           BINARY(16) NOT NULL,
    evaluation_type_id  BINARY(16) NOT NULL,

    -- Información de la evaluación
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    evaluation_date     DATE NOT NULL,

    -- Calificación
    grade               DECIMAL(4,2),
    max_grade           DECIMAL(4,2) NOT NULL DEFAULT 10.00,

    -- Estado
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_validated        BOOLEAN NOT NULL DEFAULT FALSE,
    validated_by        BINARY(16),
    validated_at        TIMESTAMP,

    -- Observaciones
    teacher_observations TEXT,
    admin_notes         TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    CONSTRAINT fk_eval_student_course
        FOREIGN KEY (student_course_subject_id)
        REFERENCES student_course_subjects(student_course_subject_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_eval_period
        FOREIGN KEY (period_id) REFERENCES evaluation_periods(period_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_eval_type
        FOREIGN KEY (evaluation_type_id) REFERENCES evaluation_types(evaluation_type_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_eval_grade CHECK (grade IS NULL OR (grade >= 0 AND grade <= max_grade)),
    CONSTRAINT chk_eval_status CHECK (status IN (
        'PENDING',    -- Pendiente de tomar
        'TAKEN',      -- Tomada, pendiente calificar
        'GRADED',     -- Calificada
        'VALIDATED',  -- Validada por administrativo
        'CANCELLED'   -- Cancelada
    )),

    INDEX idx_eval_student_course (student_course_subject_id),
    INDEX idx_eval_period (period_id),
    INDEX idx_eval_type (evaluation_type_id),
    INDEX idx_eval_date (evaluation_date),
    INDEX idx_eval_status (status),
    INDEX idx_eval_validated (is_validated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- PERIOD GRADES (Notas por Período)
-- ============================================================================

CREATE TABLE period_grades (
    period_grade_id     BINARY(16) PRIMARY KEY,
    student_course_subject_id BINARY(16) NOT NULL,
    period_id           BINARY(16) NOT NULL,

    -- Promedio del período (calculado automáticamente)
    average_grade       DECIMAL(4,2),

    -- Puede ser ajustado manualmente por el profesor
    adjusted_grade      DECIMAL(4,2),
    final_period_grade  DECIMAL(4,2),

    -- Estado
    is_passed           BOOLEAN,
    is_validated        BOOLEAN NOT NULL DEFAULT FALSE,
    validated_by        BINARY(16),
    validated_at        TIMESTAMP,

    observations        TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_pg_student_course
        FOREIGN KEY (student_course_subject_id)
        REFERENCES student_course_subjects(student_course_subject_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pg_period
        FOREIGN KEY (period_id) REFERENCES evaluation_periods(period_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_pg_grades CHECK (
        (average_grade IS NULL OR (average_grade >= 0 AND average_grade <= 10)) AND
        (adjusted_grade IS NULL OR (adjusted_grade >= 0 AND adjusted_grade <= 10)) AND
        (final_period_grade IS NULL OR (final_period_grade >= 0 AND final_period_grade <= 10))
    ),

    UNIQUE KEY uk_student_period (student_course_subject_id, period_id),

    INDEX idx_pg_student_course (student_course_subject_id),
    INDEX idx_pg_period (period_id),
    INDEX idx_pg_validated (is_validated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- FINAL GRADES (Notas Finales - Van al Libro Matriz)
-- ============================================================================

CREATE TABLE final_grades (
    final_grade_id      BINARY(16) PRIMARY KEY,
    student_course_subject_id BINARY(16) NOT NULL,
    academic_year_id    BINARY(16) NOT NULL,

    -- Promedio de períodos
    period_average      DECIMAL(4,2),

    -- Nota del examen final (si aplica)
    final_exam_grade    DECIMAL(4,2),

    -- Nota final definitiva
    final_grade         DECIMAL(4,2) NOT NULL,

    -- Estado final
    status              VARCHAR(20) NOT NULL,

    -- Validación administrativa
    is_validated        BOOLEAN NOT NULL DEFAULT FALSE,
    validated_by        BINARY(16),
    validated_at        TIMESTAMP,

    -- Registro en el Libro Matriz
    recorded_in_registry BOOLEAN NOT NULL DEFAULT FALSE,
    registry_id         BINARY(16),
    folio_number        INT,
    recorded_at         TIMESTAMP,

    observations        TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_fg_student_course
        FOREIGN KEY (student_course_subject_id)
        REFERENCES student_course_subjects(student_course_subject_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_fg_academic_year
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_fg_registry
        FOREIGN KEY (registry_id) REFERENCES qualification_registries(registry_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_fg_grades CHECK (
        (period_average IS NULL OR (period_average >= 0 AND period_average <= 10)) AND
        (final_exam_grade IS NULL OR (final_exam_grade >= 0 AND final_exam_grade <= 10)) AND
        (final_grade >= 0 AND final_grade <= 10)
    ),

    CONSTRAINT chk_fg_status CHECK (status IN (
        'PASSED',           -- Aprobado
        'FAILED',           -- Reprobado
        'PENDING_EXAM',     -- Adeuda examen
        'FREE',             -- Libre
        'ABSENT'            -- Ausente
    )),

    UNIQUE KEY uk_student_course_year (student_course_subject_id, academic_year_id),

    INDEX idx_fg_student_course (student_course_subject_id),
    INDEX idx_fg_academic_year (academic_year_id),
    INDEX idx_fg_registry (registry_id),
    INDEX idx_fg_status (status),
    INDEX idx_fg_validated (is_validated),
    INDEX idx_fg_recorded (recorded_in_registry)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
