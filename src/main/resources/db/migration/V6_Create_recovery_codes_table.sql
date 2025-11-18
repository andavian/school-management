CREATE TABLE IF NOT EXISTS recovery_codes (
    id BINARY(16) PRIMARY KEY,
    user_dni VARCHAR(20) NOT NULL,
    code_hash VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at DATETIME DEFAULT NULL,
    FOREIGN KEY (user_dni) REFERENCES users(dni) ON DELETE CASCADE,
    INDEX idx_user_dni (user_dni)
);
