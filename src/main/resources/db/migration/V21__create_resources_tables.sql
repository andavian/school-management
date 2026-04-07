-- ============================================================================
-- RESOURCES MODULE - Database Migration
-- File: src/main/resources/db/migration/V21__create_resources_tables.sql
-- BC: resources/ — Gestión de recursos didácticos y activos institucionales
-- ============================================================================

-- ============================================================================
-- RESOURCES (Catálogo / Familia de recursos)
-- ============================================================================

CREATE TABLE resources (
    resource_id         BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    code                VARCHAR(30) NOT NULL UNIQUE,    -- Ej: "NETBOOK-LENOVO", "PROYECTOR-EPSON"
    resource_type       VARCHAR(30) NOT NULL,           -- PROJECTOR, TELEVISION, LAPTOP, etc.
    description         TEXT,
    location            VARCHAR(200),                   -- Ubicación habitual: "Depósito Piso 1"
    is_reservable       BOOLEAN NOT NULL DEFAULT TRUE,
    notes               TEXT,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,

    -- Auditoría (sin FK cross-BC a users — solo UUID)
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          BINARY(16) NOT NULL,

    CONSTRAINT chk_resource_type CHECK (resource_type IN (
        'PROJECTOR',        -- Proyector
        'TELEVISION',       -- Televisor
        'LAPTOP',           -- Netbook / Notebook
        'TABLET',           -- Tablet
        'MULTIMEDIA_ROOM',  -- Sala multimedia
        'LIBRARY_ROOM',     -- Biblioteca (sala)
        'COMPUTER_LAB',     -- Laboratorio de computación
        'SPEAKER',          -- Parlante / equipo de audio
        'CAMERA',           -- Cámara fotográfica / video
        'OTHER'             -- Otro
    )),

    INDEX idx_resources_code (code),
    INDEX idx_resources_type (resource_type),
    INDEX idx_resources_active (is_active),
    INDEX idx_resources_reservable (is_reservable)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- RESOURCE UNITS (Unidades físicas individuales de cada recurso)
-- ============================================================================

CREATE TABLE resource_units (
    unit_id             BINARY(16) PRIMARY KEY,
    resource_id         BINARY(16) NOT NULL,
    unit_code           VARCHAR(50) NOT NULL UNIQUE,    -- Ej: "NETBOOK-001", "PROY-LAB-02"
    serial_number       VARCHAR(100),                   -- Número de serie del fabricante
    condition_status    VARCHAR(20) NOT NULL DEFAULT 'GOOD',  -- GOOD, FAIR, POOR
    unit_status         VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    notes               TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_units_resource
        FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_condition_status CHECK (condition_status IN (
        'GOOD',     -- Buen estado
        'FAIR',     -- Estado regular
        'POOR'      -- Mal estado / necesita revisión
    )),

    CONSTRAINT chk_unit_status CHECK (unit_status IN (
        'AVAILABLE',    -- Disponible para reservar
        'IN_USE',       -- Actualmente en uso (reserva activa)
        'MAINTENANCE',  -- En mantenimiento, no reservable
        'ON_LOAN',      -- Prestado fuera de la institución
        'RETIRED'       -- Dado de baja definitiva
    )),

    INDEX idx_units_resource (resource_id),
    INDEX idx_units_status (unit_status),
    INDEX idx_units_condition (condition_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- RESERVATIONS (Cabecera de reserva)
-- ============================================================================

CREATE TABLE reservations (
    reservation_id      BINARY(16) PRIMARY KEY,
    resource_id         BINARY(16) NOT NULL,

    -- Solicitante (sin FK cross-BC — solo UUIDs + nombre desnormalizado)
    requester_id        BINARY(16) NOT NULL,        -- userId del solicitante
    requester_name      VARCHAR(200) NOT NULL,       -- Nombre completo desnormalizado para display

    -- Horario
    reservation_date    DATE NOT NULL,
    start_time          TIME NOT NULL,
    end_time            TIME NOT NULL,

    -- Detalle de la solicitud
    quantity_requested  INT NOT NULL DEFAULT 1,
    purpose             VARCHAR(500) NOT NULL,       -- "Presentación proyecto 3°B"
    grade_level_info    VARCHAR(100),               -- Texto libre: "3°B Matemática"

    -- Estado del ciclo de vida
    status              VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',

    -- Cancelación
    cancellation_reason TEXT,
    cancelled_by        BINARY(16),                 -- userId que canceló

    -- Devolución
    return_observations TEXT,
    returned_at         TIMESTAMP,

    -- Auditoría
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservations_resource
        FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_reservation_times CHECK (end_time > start_time),
    CONSTRAINT chk_quantity CHECK (quantity_requested > 0),

    CONSTRAINT chk_reservation_status CHECK (status IN (
        'CONFIRMED',    -- Confirmada automáticamente al crear (hay stock)
        'IN_USE',       -- El recurso fue retirado / está siendo usado
        'RETURNED',     -- Devuelto, reserva completada
        'CANCELLED'     -- Cancelada por solicitante o admin
    )),

    INDEX idx_reservations_resource (resource_id),
    INDEX idx_reservations_requester (requester_id),
    INDEX idx_reservations_date (reservation_date),
    INDEX idx_reservations_status (status),
    -- Índice compuesto para consultas de disponibilidad (el más frecuente)
    INDEX idx_reservations_availability (resource_id, reservation_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- RESERVATION UNITS (Unidades físicas asignadas a cada reserva)
-- ============================================================================

CREATE TABLE reservation_units (
    reservation_unit_id BINARY(16) PRIMARY KEY,
    reservation_id      BINARY(16) NOT NULL,
    unit_id             BINARY(16) NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ru_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ru_unit
        FOREIGN KEY (unit_id) REFERENCES resource_units(unit_id)
        ON DELETE RESTRICT,

    UNIQUE KEY uk_reservation_unit (reservation_id, unit_id),

    INDEX idx_ru_reservation (reservation_id),
    INDEX idx_ru_unit (unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;