CREATE TABLE IF NOT EXISTS users
(
    user_id BINARY(16) PRIMARY KEY,
    dni VARCHAR(8) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(500) NOT NULL,
    is_active TINYINT(1) UNSIGNED DEFAULT 1,
    created_at DATETIME,
    last_login_at DATETIME DEFAULT NULL,
    updated_at DATETIME,
    INDEX idx_user_dni (dni),
    INDEX idx_user_active (is_active),
    INDEX idx_user_created (created_at),
    INDEX idx_user_roles (roles)
);