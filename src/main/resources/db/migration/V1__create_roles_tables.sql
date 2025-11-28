
CREATE TABLE roles (
    role_id     BINARY(16) NOT NULL,
    name        VARCHAR(50) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (role_id)
);


CREATE INDEX idx_roles_name ON roles(name);

INSERT INTO roles (role_id, name, created_at) VALUES
  (UNHEX(REPLACE(UUID(),'-','')), 'SUPER_ADMIN', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'ADMIN', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'PRINCIPAL', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'TEACHER', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'STUDENT', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'PARENT', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'STAFF', NOW()),
  (UNHEX(REPLACE(UUID(),'-','')), 'PRECEPTOR', NOW());

