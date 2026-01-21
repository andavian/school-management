CREATE TABLE withdrawal_reasons (
    reason_id       BINARY(16) PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    description     VARCHAR(200) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

-- ================================================
-- AGREGADO: StudentEnrollment
-- ================================================
CREATE TABLE student_enrollments (
    enrollment_id       BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL,
    academic_year_id    BINARY(16) NOT NULL,
    grade_level_id      BINARY(16) NOT NULL,

    enrollment_date     DATE NOT NULL,
    enrollment_type     VARCHAR(20) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Datos de origen
    is_repeating        BOOLEAN NOT NULL DEFAULT FALSE,
    previous_school     VARCHAR(200),
    transfer_date       DATE,

    -- Cierre del ciclo
    final_average       DECIMAL(3,2),
    passed              BOOLEAN,
    completion_date     DATE,

    -- Baja
    withdrawal_date     DATE,
    withdrawal_reason_id BINARY(16),
    withdrawal_observations TEXT,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES student_personal_data(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(grade_level_id) ON DELETE RESTRICT,
    FOREIGN KEY (withdrawal_reason_id) REFERENCES withdrawal_reasons(reason_id) ON DELETE RESTRICT,

    UNIQUE KEY unique_student_year_grade (student_id, academic_year_id, grade_level_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
);




