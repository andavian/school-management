CREATE TABLE IF NOT EXISTS users
(
    user_id BINARY(16) PRIMARY KEY,
    email VARCHAR(80) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active TINYINT(1) UNSIGNED DEFAULT 1,
    reset_password_token VARCHAR(100),
    reset_password_token_expiry DATETIME DEFAULT NULL,
    refresh_token VARCHAR(255) DEFAULT NULL,
    confirmation_code VARCHAR(100),
    recovery_code VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);