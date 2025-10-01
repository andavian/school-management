CREATE TABLE IF NOT EXISTS password_resets (
    id VARCHAR(36) PRIMARY KEY,
    user_dni VARCHAR(20) NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    used_at DATETIME DEFAULT NULL,
    FOREIGN KEY (user_dni) REFERENCES users(dni) ON DELETE CASCADE,
    INDEX idx_user_dni (user_dni),
    INDEX idx_expires_at (expires_at)
);
