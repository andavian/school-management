-- V19__seed_countries_provinces.sql
-- Seeder: Country + Provinces (Argentina)

-- Insert country: Argentina
INSERT INTO countries (country_id, name, iso_code, phone_code, created_at)
VALUES (
    UNHEX(REPLACE(UUID(), '-', '')),
    'Argentina',
    'ARG',
    '+54',
    CURRENT_TIMESTAMP
);

-- Insert provinces of Argentina (codes as agreed)
INSERT INTO provinces (province_id, country_id, name, code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Buenos Aires', 'BA', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Ciudad Autónoma de Buenos Aires', 'CABA', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Catamarca', 'CAT', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Chaco', 'CHA', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Chubut', 'CHU', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Córdoba', 'CBA', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Corrientes', 'COR', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Entre Ríos', 'ER', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Formosa', 'FOR', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Jujuy', 'JUJ', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'La Pampa', 'LP', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'La Rioja', 'LR', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Mendoza', 'MZA', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Misiones', 'MIS', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Neuquén', 'NEU', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Río Negro', 'RN', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Salta', 'SAL', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'San Juan', 'SJ', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'San Luis', 'SL', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Santa Cruz', 'SC', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Santa Fe', 'SF', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Santiago del Estero', 'SE', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Tierra del Fuego', 'TF', CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT country_id FROM countries WHERE iso_code='ARG'), 'Tucumán', 'TUC', CURRENT_TIMESTAMP);
