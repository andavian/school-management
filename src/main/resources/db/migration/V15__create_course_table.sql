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
-- COURSE SUBJECTS (CursoMateria - Materia asignada a un curso específico)
-- ============================================================================

CREATE TABLE course_subjects (
    course_subject_id   BINARY(16) PRIMARY KEY,
    grade_level_id      BINARY(16) NOT NULL,
    subject_id          BINARY(16) NOT NULL,
    teacher_id          BINARY(16),
    academic_year_id    BINARY(16) NOT NULL,

    -- Horarios (JSON o tabla separada)
    schedule_json       JSON,
    classroom           VARCHAR(50),

    -- Configuración de evaluación
    min_passing_grade   DECIMAL(4,2) NOT NULL DEFAULT 6.00,

    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cs_grade_level
        FOREIGN KEY (grade_level_id) REFERENCES grade_levels(grade_level_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_cs_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_cs_academic_year
        FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_cs_passing_grade CHECK (min_passing_grade BETWEEN 1 AND 10),
    CONSTRAINT chk_cs_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED')),

    UNIQUE KEY uk_course_subject (grade_level_id, subject_id, academic_year_id),

    INDEX idx_cs_grade_level (grade_level_id),
    INDEX idx_cs_subject (subject_id),
    INDEX idx_cs_teacher (teacher_id),
    INDEX idx_cs_academic_year (academic_year_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- STUDENT COURSE SUBJECTS (AlumnoMateria - Inscripción del alumno)
-- ============================================================================

CREATE TABLE student_course_subjects (
    student_course_subject_id BINARY(16) PRIMARY KEY,
    enrollment_id       BINARY(16) NOT NULL,
    course_subject_id   BINARY(16) NOT NULL,

    -- Estado de cursada
    status              VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',

    -- Asistencia
    total_classes       INT DEFAULT 0,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_scs_enrollment
        FOREIGN KEY (enrollment_id) REFERENCES student_enrollments(enrollment_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_scs_course_subject
        FOREIGN KEY (course_subject_id) REFERENCES course_subjects(course_subject_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_scs_status CHECK (status IN (
        'ENROLLED',      -- Inscripto
        'ATTENDING',     -- Cursando
        'PASSED',        -- Aprobado
        'FAILED',        -- Reprobado
        'PENDING_EXAM',  -- Adeuda examen
        'FREE',          -- Libre por inasistencias
        'WITHDRAWN'      -- Retirado
    )),


    UNIQUE KEY uk_enrollment_course_subject (enrollment_id, course_subject_id),

    INDEX idx_scs_enrollment (enrollment_id),
    INDEX idx_scs_course_subject (course_subject_id),
    INDEX idx_scs_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;