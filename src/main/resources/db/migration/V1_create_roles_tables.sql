
CREATE TABLE roles (
    role_id     BINARY(16) NOT NULL,
    name        VARCHAR(50) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (role_id)
);


CREATE TABLE user_roles (
    user_id     BINARY(16) NOT NULL,
    role_id     BINARY(16) NOT NULL,
    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);


CREATE INDEX idx_roles_name ON roles(name);

INSERT INTO roles (role_id, name, created_at) VALUES
    (UUID_TO_BIN(UUID()), 'SUPER_ADMIN', NOW());
    (UUID_TO_BIN(UUID()), 'ADMIN', NOW()),
    (UUID_TO_BIN(UUID()), 'TEACHER', NOW()),
    (UUID_TO_BIN(UUID()), 'STUDENT', NOW()),
    (UUID_TO_BIN(UUID()), 'PARENT', NOW()),
    (UUID_TO_BIN(UUID()), 'STAFF', NOW());
    (UUID_TO_BIN(UUID()), 'PRECEPTOR', NOW());
