CREATE TABLE IF NOT EXISTS blacklisted_tokens (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    token_type VARCHAR(20) NOT NULL,
    blacklisted_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    reason VARCHAR(100),
    user_email VARCHAR(254) NOT NULL,
    INDEX idx_token_hash (token_hash),
    INDEX idx_expires_at (expires_at),
    INDEX idx_user_email (user_email)
);