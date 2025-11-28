CREATE TABLE withdrawal_reasons (
    reason_id       BINARY(16) PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    description     VARCHAR(200) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

-- enrollment table
CREATE TABLE enrollments (
    enrollment_id       BINARY(16) PRIMARY KEY,
    student_id          BINARY(16) NOT NULL,
    grade_level_id      BINARY(16) NOT NULL,
    academic_year_id    BINARY(16) NOT NULL,

    enrollment_date     DATE NOT NULL,

    enrollment_type VARCHAR(20) NOT NULL,
    CHECK (enrollment_type IN ('NEW','RETURNING','TRANSFER')),

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CHECK (status IN ('ACTIVE','WITHDRAWN','COMPLETED')),

    withdrawal_reason_id BINARY(16) NULL,
    withdrawal_notes TEXT NULL,


    is_repeating        BOOLEAN NOT NULL DEFAULT FALSE,
    previous_school     VARCHAR(200),
    transfer_date       DATE,

    -- Auditoría de cierre del ciclo
    completion_date     DATE,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(grade_level_id) ON DELETE RESTRICT,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (withdrawal_reason_id) REFERENCES withdrawal_reasons(reason_id),

    UNIQUE KEY unique_student_grade_year (student_id, grade_level_id, academic_year_id),
    INDEX idx_student_id (student_id),
    INDEX idx_grade_level (grade_level_id),
    INDEX idx_academic_year (academic_year_id),
    INDEX idx_status (status)
);




