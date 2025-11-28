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
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id     BINARY(16) NOT NULL,
    role_id     BINARY(16) NOT NULL,
    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);