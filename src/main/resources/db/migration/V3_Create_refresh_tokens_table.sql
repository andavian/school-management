CREATE TABLE IF NOT EXISTS refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_dni VARCHAR(20) NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    revoked_at DATETIME DEFAULT NULL,
    device_info VARCHAR(200),
    FOREIGN KEY (user_dni) REFERENCES users(dni) ON DELETE CASCADE,
    INDEX idx_user_dni (user_dni),
    INDEX idx_expires_at (expires_at)
);
