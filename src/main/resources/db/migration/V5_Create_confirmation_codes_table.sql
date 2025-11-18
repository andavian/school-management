CREATE TABLE IF NOT EXISTS confirmation_codes (
    id BINARY(16) PRIMARY KEY,
    user_dni VARCHAR(20) NOT NULL,
    code VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    used_at DATETIME DEFAULT NULL,
    FOREIGN KEY (user_dni) REFERENCES users(dni) ON DELETE CASCADE,
    INDEX idx_user_dni (user_dni),
    INDEX idx_expires_at (expires_at)
);
