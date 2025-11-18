CREATE TABLE enrollments (
    enrollment_id       BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL,
    grade_level_id      BINARY(16) NOT NULL,
    academic_year_id    BINARY(16) NOT NULL,

    enrollment_date     DATE NOT NULL,
    enrollment_type     VARCHAR(20) NOT NULL,  -- NEW, RETURNING, TRANSFER
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, WITHDRAWN, COMPLETED

    -- Información de progreso
    is_repeating        BOOLEAN NOT NULL DEFAULT FALSE,
    previous_school     VARCHAR(200),
    transfer_date       DATE,

    -- Calificaciones finales
    final_average       DECIMAL(5,2),
    passed              BOOLEAN,
    completion_date     DATE,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(grade_level_id) ON DELETE RESTRICT,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,

    UNIQUE KEY unique_student_grade_year (student_id, grade_level_id, academic_year_id),
    INDEX idx_student_id (student_id),
    INDEX idx_grade_level (grade_level_id),
    INDEX idx_academic_year (academic_year_id),
    INDEX idx_status (status)
);