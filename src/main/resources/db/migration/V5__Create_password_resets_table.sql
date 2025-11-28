CREATE TABLE IF NOT EXISTS password_resets (
    id BINARY(16) PRIMARY KEY,
    user_dni VARCHAR(20) NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    used_at DATETIME DEFAULT NULL,
    INDEX idx_user_dni (user_dni),
    INDEX idx_expires_at (expires_at),
     CONSTRAINT fk_password_resets_user
                FOREIGN KEY (user_dni)
                REFERENCES users (dni)
                ON DELETE CASCADE
    )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
