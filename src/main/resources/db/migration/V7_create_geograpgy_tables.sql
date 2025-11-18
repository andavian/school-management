CREATE TABLE countries (
    country_id          BINARY(16) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    iso_code            VARCHAR(3) NOT NULL UNIQUE,  -- ARG, BRA, CHL
    phone_code          VARCHAR(10),                 -- +54, +55, +56
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

        INDEX idx_countries_iso_code (iso_code),
        INDEX idx_countries_name (name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE provinces (
    province_id         BINARY(16) PRIMARY KEY,
    country_id          BINARY(16) NOT NULL,
    name                VARCHAR(100) NOT NULL,
    code                VARCHAR(10),  -- CBA (Córdoba), BA (Buenos Aires)
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_provinces_country FOREIGN KEY (country_id)
            REFERENCES countries(country_id) ON DELETE RESTRICT,

        CONSTRAINT uk_province_name_country UNIQUE (country_id, name),

        INDEX idx_provinces_country (country_id),
        INDEX idx_provinces_name (name),
        INDEX idx_provinces_code (code)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE places (
    place_id            BINARY(16) PRIMARY KEY,
    province_id         BINARY(16) NOT NULL,
    name                VARCHAR(100) NOT NULL,
    type                VARCHAR(20) NOT NULL,  -- CIUDAD, LOCALIDAD, MUNICIPIO
    postal_code         VARCHAR(10),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT fk_places_province FOREIGN KEY (province_id)
           REFERENCES provinces(province_id) ON DELETE RESTRICT,

       CONSTRAINT uk_place_name_province UNIQUE (province_id, name),

       INDEX idx_places_province (province_id),
       INDEX idx_places_name (name),
       INDEX idx_places_type (type),
       INDEX idx_places_postal_code (postal_code)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
