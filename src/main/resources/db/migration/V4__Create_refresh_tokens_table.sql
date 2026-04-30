CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BINARY(16) PRIMARY KEY,

    user_dni VARCHAR(20) NOT NULL,

    token_hash VARCHAR(64) NOT NULL UNIQUE,

    issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,

    revoked_at DATETIME DEFAULT NULL,

    replaced_by_token_hash VARCHAR(64) DEFAULT NULL,

    device_info VARCHAR(200),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    version BIGINT DEFAULT 0,

    INDEX idx_user_dni (user_dni),
    INDEX idx_token_hash (token_hash),
    INDEX idx_expires_at (expires_at),
    INDEX idx_user_active (user_dni, revoked_at),

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_dni)
        REFERENCES users (dni)
        ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;