-- V31__seed_places.sql
-- Seeder: Places (10 principales ciudades por provincia) + Localidades de Córdoba

-- *****************************************
-- Ciudades principales por provincia (10 por provincia)
-- *****************************************

-- Buenos Aires (BA)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'La Plata', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Mar del Plata', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Bahía Blanca', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Tandil', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Quilmes', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Lanús', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Olavarría', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Pergamino', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'Junín', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='BA'), 'San Nicolás', 'CIUDAD', NULL, CURRENT_TIMESTAMP);

-- Ciudad Autónoma de Buenos Aires (CABA)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Ciudad Autónoma de Buenos Aires', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Palermo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Recoleta', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Belgrano', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'San Telmo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Caballito', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'La Boca', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Belgrano R', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Flores', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CABA'), 'Núñez', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Catamarca (CAT)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'San Fernando del Valle de Catamarca', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Tinogasta', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Santa María', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Andalgalá', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Fray Mamerto Esquiú', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Capital', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Belén', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Capayán', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CAT'), 'Antofagasta de la Sierra', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Chaco (CHA)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Resistencia', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Presidencia Roque Sáenz Peña', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Corzuela', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Charata', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Juan José Castelli', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'San Fernando', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'General Pinedo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'General San Martín', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Sáenz Peña', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHA'), 'Villa Ángela', 'CIUDAD', NULL, CURRENT_TIMESTAMP);

-- Chubut (CHU)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Rawson', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Trelew', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Comodoro Rivadavia', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Puerto Madryn', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Madryn', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Esquel', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Sarmiento', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Gaiman', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Dolavon', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CHU'), 'Camarones', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);


-- Corrientes (COR)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Corrientes', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Resistencia', 'CIUDAD', NULL, CURRENT_TIMESTAMP), -- note: Resistencia is Chaco capital but kept as major nearby city; adjust if undesired
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Goya', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Monte Caseros', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Paso de los Libres', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Mercedes', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Ituzaingó', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Curuzú Cuatiá', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Lavalle', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='COR'), 'Enrique Mosconi', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Entre Ríos (ER)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Paraná', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Concepción del Uruguay', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Gualeguaychú', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Victoria', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Crespo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Nogoyá', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Chajarí', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'La Paz', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Basavilbaso', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='ER'), 'Urdinarrain', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Formosa (FOR)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Formosa', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Clorinda', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Pirané', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'El Colorado', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Ibarreta', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Palo Santo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Laguna Naick Neck', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Subteniente Perín', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'El Espinillo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='FOR'), 'Riacho He Hé', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Jujuy (JUJ)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'San Salvador de Jujuy', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Perico', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Libertador General San Martín', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Palpalá', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Tilcara', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Humahuaca', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Yuto', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'El Carmen', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Fraile Pintado', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='JUJ'), 'Maimará', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- La Pampa (LP)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Santa Rosa', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'General Pico', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Toay', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Eduardo Castex', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Victorica', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Realico', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Winifreda', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Rancul', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Quetrequén', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LP'), 'Catriló', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- La Rioja (LR)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'La Rioja', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Chilecito', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Aimogasta', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Vichigasta', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Chepes', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Chamical', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Famatina', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Guandacol', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Nonogasta', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='LR'), 'Los Colorados', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Mendoza (MZA)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Mendoza', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Godoy Cruz', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'San Rafael', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Luján de Cuyo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'General Alvear', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'San Martín', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Godoy', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Tunuyán', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Tupungato', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MZA'), 'Las Heras', 'CIUDAD', NULL, CURRENT_TIMESTAMP);

-- Misiones (MIS)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Posadas', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Eldorado', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Oberá', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Puerto Iguazú', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Capioví', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Montecarlo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Leandro N. Alem', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'San Vicente', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Apóstoles', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='MIS'), 'Iguazú', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Neuquén (NEU)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Neuquén', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Plottier', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Cutral Có', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Centenario', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'San Martín de los Andes', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Zapala', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Chos Malal', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Las Lajas', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'San Patricio del Chañar', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='NEU'), 'Picún Leufú', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Río Negro (RN)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Viedma', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Bariloche', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'General Roca', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Cipolletti', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Allen', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Río Colorado', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'San Carlos de Bariloche', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Choele Choel', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Pomona', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='RN'), 'Viedma Playas', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Salta (SAL)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Salta', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'San Ramón de la Nueva Orán', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Tartagal', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Cafayate', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Metán', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'General Güemes', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Campo Santo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Embarcación', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Pichanal', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SAL'), 'Rosario de la Frontera', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- San Juan (SJ)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'San Juan', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Rivadavia', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Rawson', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Pocito', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Santa Lucía', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Valle Fértil', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Sarmiento', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Caucete', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Zonda', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SJ'), 'Ullum', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- San Luis (SL)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'San Luis', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'Villa Mercedes', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'Merlo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'La Toma', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'Tilisarao', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'San Francisco', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'Quines', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'Concarán', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'La Carolina', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SL'), 'Buena Esperanza', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Santa Cruz (SC)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Río Gallegos', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Caleta Olivia', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'El Calafate', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Las Heras', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Pico Truncado', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Puerto Deseado', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'El Chaltén', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Perito Moreno', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Gobernador Gregores', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SC'), 'Comandante Luis Piedra Buena', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Santa Fe (SF)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Santa Fe', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Rosario', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Rafaela', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Venado Tuerto', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Reconquista', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Casilda', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'San Lorenzo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Villa Gobernador Gálvez', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'Cañada de Gómez', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SF'), 'San Cristóbal', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Santiago del Estero (SE)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Santiago del Estero', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'La Banda', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Termas de Río Hondo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Sumampa', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Añatuya', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Frías', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'La Cañada', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Loreto', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Quimilí', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='SE'), 'Choya', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Tierra del Fuego (TF)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Ushuaia', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Río Grande', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Tolhuin', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Cabo San Pablo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Estancia', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Alto Río Cóndor', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Paraje', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Was', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Kilómetro', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TF'), 'Fagnano', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- Tucumán (TUC)
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'San Miguel de Tucumán', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Yerba Buena', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Concepción', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Tafí Viejo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Famaillá', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Río Chico', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Burruyacú', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Santiago del Estero (frontera)', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Aguilares', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='TUC'), 'Chicligasta', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);

-- *****************************************
-- Localidades y ciudades representativas de la Provincia de Córdoba (CBA)
-- *****************************************
-- Incluye capital, ciudades importantes, villas turísticas, cabeceras de departamentos y localidades relevantes.
INSERT INTO places (place_id, province_id, name, type, postal_code, created_at) VALUES
-- Capital y grandes ciudades
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Córdoba', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Villa Carlos Paz', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Río Cuarto', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Villa María', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'San Francisco', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Río Tercero', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Bell Ville', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Jesús María', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Alta Gracia', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Cosquín', 'CIUDAD', NULL, CURRENT_TIMESTAMP),

-- Ciudades promedio / cabeceras de departamento
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Villa Allende', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Río Segundo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'San Antonio de Arredondo', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Falda', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Huerta Grande', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Capilla del Monte', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Cruz del Eje', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Oncativo', 'CIUDAD', NULL, CURRENT_TIMESTAMP),

-- Localidades importantes y turísticas
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Villa Dolores', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Nono', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Cumbre', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Villa Cura Brochero', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Deán Funes', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Aldea Santa María', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Calera', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Colonia Caroya', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Corralito', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),

-- Más localidades de Córdoba (lista representativa)
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Ballesteros', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Alta Italia', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Arroyito', 'CIUDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Arias', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Ballesteros Sud', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Balnearia', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Camilo Aldao', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Canals', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),


(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Despeñaderos', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Dique Chico', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Edén', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'El Tío', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'El Talar', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Estación Juárez Celman', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Etruria', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'General Deheza', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),

(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'General Cabrera', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'General Levalle', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'General Roca', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Guatimozín', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Hernando', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Inriville', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Isla Verde', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Carlota', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Palestina', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Playa', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),

(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Las Varillas', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'La Para', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Las Mojarras', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Leones', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Los Cóndores', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Mendiolaza', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Monte Buey', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Morteros', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Mina Clavero', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Oliva', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),

(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Padilla', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Pilar', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Río Primero', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Salsipuedes', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'San Marcos Sierras', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'San Javier', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Suco', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP),
(UNHEX(REPLACE(UUID(), '-', '')), (SELECT province_id FROM provinces WHERE code='CBA'), 'Ticino', 'LOCALIDAD', NULL, CURRENT_TIMESTAMP);
