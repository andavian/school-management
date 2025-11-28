-- ============================================================================
-- INITIAL DATA
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
    150,
    1,
    150,
    'ACTIVE',
    CURRENT_TIMESTAMP
);

INSERT INTO withdrawal_reasons (reason_id, code, description)
VALUES
(UUID_TO_BIN(UUID()), 'TRANSFER', 'Pase a otra institución'),
(UUID_TO_BIN(UUID()), 'ABANDONMENT', 'Abandono escolar'),
(UUID_TO_BIN(UUID()), 'RESIDENCE_CHANGE', 'Cambio de domicilio'),
(UUID_TO_BIN(UUID()), 'MEDICAL', 'Baja médica'),
(UUID_TO_BIN(UUID()), 'ADULT_ED', 'Derivado a educación para adultos'),
(UUID_TO_BIN(UUID()), 'EXPELLED', 'Expulsión'),
(UUID_TO_BIN(UUID()), 'OTHER', 'Otro motivo');

-- Evaluation Types
INSERT INTO evaluation_types (evaluation_type_id, name, code, description, weight_percentage) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Trabajo Práctico', 'TP', 'Trabajos prácticos y ejercitaciones', 30.00),
(UNHEX(REPLACE(UUID(), '-', '')), 'Examen Parcial', 'PARCIAL', 'Exámenes parciales', 40.00),
(UNHEX(REPLACE(UUID(), '-', '')), 'Examen Final', 'FINAL', 'Examen final de la materia', 100.00),
(UNHEX(REPLACE(UUID(), '-', '')), 'Proyecto', 'PROYECTO', 'Proyectos integradores', 30.00),
(UNHEX(REPLACE(UUID(), '-', '')), 'Exposición Oral', 'ORAL', 'Presentaciones orales', 20.00),
(UNHEX(REPLACE(UUID(), '-', '')), 'Laboratorio', 'LAB', 'Trabajos de laboratorio', 25.00);

-- Evaluation Periods for 2024
SET @year_2024_id = (SELECT academic_year_id FROM academic_years WHERE year = 2024);

INSERT INTO evaluation_periods (period_id, academic_year_id, period_number, name, start_date, end_date, is_current, status) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), @year_2024_id, 1, 'Primer Cuatrimestre', '2024-03-01', '2024-07-15', TRUE, 'ACTIVE'),
(UNHEX(REPLACE(UUID(), '-', '')), @year_2024_id, 2, 'Segundo Cuatrimestre', '2024-08-01', '2024-12-15', FALSE, 'PENDING');

-- Study Plans (Plan de Estudios 2024)
INSERT INTO study_plans (study_plan_id, name, code, description, orientation_id, year_level, is_active) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), 'Plan Ciclo Básico 1° Año', 'CB-2024-1', 'Plan de estudios para 1° año', NULL, 1, TRUE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Plan Ciclo Básico 2° Año', 'CB-2024-2', 'Plan de estudios para 2° año', NULL, 2, TRUE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Plan Ciclo Básico 3° Año', 'CB-2024-3', 'Plan de estudios para 3° año', NULL, 3, TRUE);


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
