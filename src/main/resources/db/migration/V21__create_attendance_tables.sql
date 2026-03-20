-- ============================================================================
-- ATTENDANCE MODULE — V21
-- Tablas de asistencia diaria, por materia y resúmenes por período
-- IPET 132 — Mínimo 85% asistencia
-- ============================================================================

-- ================================================
-- attendance_daily_records
-- Lista de presentes tomada por el preceptor (STAFF)
-- ================================================
CREATE TABLE attendance_daily_records (
    daily_attendance_id     BINARY(16) PRIMARY KEY,
    student_id              BINARY(16) NOT NULL,
    grade_level_id          BINARY(16) NOT NULL,
    academic_year_id        BINARY(16) NOT NULL,
    attendance_date         DATE NOT NULL,

    -- PRESENT | ABSENT | JUSTIFIED | LATE | WITHDRAWN
    status                  VARCHAR(20) NOT NULL,
    justification_reason    VARCHAR(500),
    observations            VARCHAR(500),

    recorded_by_user_id     BINARY(16) NOT NULL,
    corrected_by_user_id    BINARY(16),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_dar_student
        FOREIGN KEY (student_id)
        REFERENCES student_personal_data(student_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_dar_grade_level
        FOREIGN KEY (grade_level_id)
        REFERENCES grade_levels(grade_level_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_dar_academic_year
        FOREIGN KEY (academic_year_id)
        REFERENCES academic_years(academic_year_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_dar_status
        CHECK (status IN ('PRESENT', 'ABSENT', 'JUSTIFIED', 'LATE', 'WITHDRAWN')),

    UNIQUE KEY uk_daily_student_date (student_id, attendance_date),

    INDEX idx_dar_student (student_id),
    INDEX idx_dar_grade_level_date (grade_level_id, attendance_date),
    INDEX idx_dar_academic_year (academic_year_id),
    INDEX idx_dar_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- attendance_course_records
-- Asistencia por clase tomada por el docente (TEACHER)
-- ================================================
CREATE TABLE attendance_course_records (
    course_attendance_id        BINARY(16) PRIMARY KEY,
    student_course_subject_id   BINARY(16) NOT NULL,
    course_subject_id           BINARY(16) NOT NULL,
    period_id                   BINARY(16) NOT NULL,
    class_date                  DATE NOT NULL,

    -- PRESENT | ABSENT | LATE | WITHDRAWN
    status                      VARCHAR(20) NOT NULL,
    observations                VARCHAR(500),

    recorded_by_user_id         BINARY(16) NOT NULL,
    corrected_by_user_id        BINARY(16),

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_car_student_course_subject
        FOREIGN KEY (student_course_subject_id)
        REFERENCES student_course_subjects(student_course_subject_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_car_course_subject
        FOREIGN KEY (course_subject_id)
        REFERENCES course_subjects(course_subject_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_car_period
        FOREIGN KEY (period_id)
        REFERENCES evaluation_periods(period_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_car_status
        CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'WITHDRAWN')),

    UNIQUE KEY uk_course_student_date (student_course_subject_id, class_date),

    INDEX idx_car_student_course_subject (student_course_subject_id),
    INDEX idx_car_course_subject_date (course_subject_id, class_date),
    INDEX idx_car_period (period_id),
    INDEX idx_car_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- attendance_period_summaries
-- Resumen calculado automáticamente por período y materia
-- ================================================
CREATE TABLE attendance_period_summaries (
    attendance_summary_id       BINARY(16) PRIMARY KEY,
    student_course_subject_id   BINARY(16) NOT NULL,
    course_subject_id           BINARY(16) NOT NULL,
    period_id                   BINARY(16) NOT NULL,

    -- Contadores
    total_classes       INT NOT NULL DEFAULT 0,
    present_count       INT NOT NULL DEFAULT 0,
    absent_count        INT NOT NULL DEFAULT 0,
    justified_count     INT NOT NULL DEFAULT 0,
    late_count          INT NOT NULL DEFAULT 0,
    withdrawn_count     INT NOT NULL DEFAULT 0,

    -- Cálculos ponderados (reglas IPET 132)
    -- ABSENT=1.0 | JUSTIFIED=1.0 | LATE=0.2 | WITHDRAWN=0.2 | PRESENT=0.0
    weighted_absences       DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    attendance_percentage   DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    at_risk                 BOOLEAN NOT NULL DEFAULT FALSE,

    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_aps_student_course_subject
        FOREIGN KEY (student_course_subject_id)
        REFERENCES student_course_subjects(student_course_subject_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_aps_course_subject
        FOREIGN KEY (course_subject_id)
        REFERENCES course_subjects(course_subject_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_aps_period
        FOREIGN KEY (period_id)
        REFERENCES evaluation_periods(period_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_aps_attendance_percentage
        CHECK (attendance_percentage BETWEEN 0 AND 100),

    UNIQUE KEY uk_summary_student_period (student_course_subject_id, period_id),

    INDEX idx_aps_student_course_subject (student_course_subject_id),
    INDEX idx_aps_course_subject_period (course_subject_id, period_id),
    INDEX idx_aps_at_risk (at_risk),
    INDEX idx_aps_period (period_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
