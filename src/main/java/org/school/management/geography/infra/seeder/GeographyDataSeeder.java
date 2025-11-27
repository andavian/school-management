package org.school.management.geography.infra.seeder;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.CountryRepository;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.PlaceType;
import org.school.management.geography.infra.persistence.repository.CountryJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Seeder para Geography Module
 * Puebla la base de datos con datos geográficos de Argentina
 *
 * @Order(1) para ejecutar antes que otros seeders que dependan de Geography
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class GeographyDataSeeder implements ApplicationRunner {

    private final CountryJpaRepository countryJpaRepository;
    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final PlaceRepository placeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Geography Data Seeder...");
        log.info("=".repeat(80));

        // Verificar si ya existen datos
        if (countryRepository.count() > 0) {
            log.info("Geography data already exists. Skipping seeder.");
            return;
        }

        try {
            seedArgentina();
            log.info("=".repeat(80));
            log.info("Geography Data Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding geography data", e);
            throw e;
        }
    }

    private void seedArgentina() {
        log.info("Seeding Argentina data...");

        // 1. Crear Argentina
        Country argentina = createArgentina();

        // 2. Crear provincias
        Map<String, Province> provinces = createProvinces(argentina.getCountryId());

        // 3. Crear lugares principales de Córdoba
        createCordobaPlaces(provinces.get("Córdoba"));

        // 4. Crear lugares principales de otras provincias
        createBuenosAiresPlaces(provinces.get("Buenos Aires"));
        createSantaFePlaces(provinces.get("Santa Fe"));
        createMendozaPlaces(provinces.get("Mendoza"));

        log.info("Argentina data seeded successfully");
    }

    private Country createArgentina() {
        UUID argentinaId = UUID.fromString("242aa839-083b-4c38-b567-a4727f8f1030");

        // Usamos el repositorio JPA para la comprobación, que es más eficiente
        if (countryJpaRepository.existsById(argentinaId)) {
            log.info("Country Argentina already exists. Fetching from database.");
            // Si existe, lo recuperamos desde el repositorio de dominio para obtener el objeto completo
            return countryRepository.findById(CountryId.of(argentinaId))
                    .orElseThrow(() -> new IllegalStateException("Country with ID " + argentinaId + " should exist but was not found."));
        } else {
            // Si no existe, lo creamos usando el ID FIJO
            log.info("Creating country: Argentina");
            Country argentina = Country.withId( // <-- Usamos el nuevo método de fábrica
                    argentinaId,
                    "Argentina",
                    "ARG",
                    "+54"
            );
            argentina = countryRepository.save(argentina);

            log.info("✓ Country created: {} ({})",
                    argentina.getNameAsString(),
                    argentina.getIsoCodeAsString());

            return argentina;
        }
    }

    private Map<String, Province> createProvinces(CountryId argentinaId) {
        log.info("Creating provinces of Argentina...");

        // <-- CAMBIO AQUÍ: Usa un Map<String, String> más simple
        Map<String, String> provincesData = Map.ofEntries(
                Map.entry("Buenos Aires", "BA"),
                Map.entry("Catamarca", "CT"),
                Map.entry("Chaco", "CC"),
                Map.entry("Chubut", "CH"),
                Map.entry("Córdoba", "CBA"),
                Map.entry("Corrientes", "CR"),
                Map.entry("Entre Ríos", "ER"),
                Map.entry("Formosa", "FO"),
                Map.entry("Jujuy", "JY"),
                Map.entry("La Pampa", "LP"),
                Map.entry("La Rioja", "LR"),
                Map.entry("Mendoza", "MZ"),
                Map.entry("Misiones", "MI"),
                Map.entry("Neuquén", "NQ"),
                Map.entry("Río Negro", "RN"),
                Map.entry("Salta", "SA"),
                Map.entry("San Juan", "SJ"),
                Map.entry("San Luis", "SL"),
                Map.entry("Santa Cruz", "SC"),
                Map.entry("Santa Fe", "SF"),
                Map.entry("Santiago del Estero", "SE"),
                Map.entry("Tierra del Fuego", "TF"),
                Map.entry("Tucumán", "TU"),
                Map.entry("Ciudad Autónoma de Buenos Aires", "CABA") // <-- Sin null, más limpio
        );

        Map<String, Province> createdProvinces = new java.util.HashMap<>();

        for (var entry : provincesData.entrySet()) {
            String name = entry.getKey();
            String code = entry.getValue(); // <-- Ahora obtienes el código directamente

            Province province = Province.create(name, code, argentinaId);
            province = provinceRepository.save(province);

            createdProvinces.put(name, province);
            log.info("  ✓ Province: {} ({})", name, code);
        }

        log.info("✓ Created {} provinces", createdProvinces.size());

        return createdProvinces;
    }

        private void createCordobaPlaces(Province cordoba) {
        log.info("Creating places in Córdoba province...");

        List<PlaceData> places = List.of(
                new PlaceData("Córdoba Capital", PlaceType.CIUDAD, "5000"),
                new PlaceData("Alta Gracia", PlaceType.CIUDAD, "5186"),
                new PlaceData("Villa Carlos Paz", PlaceType.CIUDAD, "5152"),
                new PlaceData("Río Cuarto", PlaceType.CIUDAD, "5800"),
                new PlaceData("Villa María", PlaceType.CIUDAD, "5900"),
                new PlaceData("San Francisco", PlaceType.CIUDAD, "2400"),
                new PlaceData("Bell Ville", PlaceType.CIUDAD, "2550"),
                new PlaceData("Río Tercero", PlaceType.CIUDAD, "5850"),
                new PlaceData("Villa Allende", PlaceType.CIUDAD, "5105"),
                new PlaceData("Jesús María", PlaceType.CIUDAD, "5220"),
                new PlaceData("La Falda", PlaceType.CIUDAD, "5172"),
                new PlaceData("Villa Dolores", PlaceType.CIUDAD, "5870"),
                new PlaceData("Cosquín", PlaceType.CIUDAD, "5166"),
                new PlaceData("Cruz del Eje", PlaceType.CIUDAD, "5280"),
                new PlaceData("Laboulaye", PlaceType.CIUDAD, "6120"),
                new PlaceData("Río Segundo", PlaceType.CIUDAD, "5960"),
                new PlaceData("Villa del Rosario", PlaceType.CIUDAD, "5963"),
                new PlaceData("Dean Funes", PlaceType.CIUDAD, "5200"),
                new PlaceData("Marcos Juárez", PlaceType.CIUDAD, "2580"),
                new PlaceData("Arroyito", PlaceType.CIUDAD, "2434")
        );

        savePlaces(places, cordoba);

        log.info("✓ Created {} places in Córdoba", places.size());
    }

    private void createBuenosAiresPlaces(Province buenosAires) {
        log.info("Creating places in Buenos Aires province...");

        List<PlaceData> places = List.of(
                new PlaceData("La Plata", PlaceType.CIUDAD, "1900"),
                new PlaceData("Mar del Plata", PlaceType.CIUDAD, "7600"),
                new PlaceData("Bahía Blanca", PlaceType.CIUDAD, "8000"),
                new PlaceData("Tandil", PlaceType.CIUDAD, "7000"),
                new PlaceData("San Isidro", PlaceType.CIUDAD, "1642"),
                new PlaceData("Quilmes", PlaceType.CIUDAD, "1878"),
                new PlaceData("Avellaneda", PlaceType.CIUDAD, "1870"),
                new PlaceData("Lomas de Zamora", PlaceType.CIUDAD, "1832"),
                new PlaceData("Lanús", PlaceType.CIUDAD, "1824")
        );

        savePlaces(places, buenosAires);

        log.info("✓ Created {} places in Buenos Aires", places.size());
    }

    private void createSantaFePlaces(Province santaFe) {
        log.info("Creating places in Santa Fe province...");

        List<PlaceData> places = List.of(
                new PlaceData("Santa Fe Capital", PlaceType.CIUDAD, "3000"),
                new PlaceData("Rosario", PlaceType.CIUDAD, "2000"),
                new PlaceData("Rafaela", PlaceType.CIUDAD, "2300"),
                new PlaceData("Venado Tuerto", PlaceType.CIUDAD, "2600"),
                new PlaceData("Reconquista", PlaceType.CIUDAD, "3560"),
                new PlaceData("Villa Constitución", PlaceType.CIUDAD, "2919")
        );

        savePlaces(places, santaFe);

        log.info("✓ Created {} places in Santa Fe", places.size());
    }

    private void createMendozaPlaces(Province mendoza) {
        log.info("Creating places in Mendoza province...");

        List<PlaceData> places = List.of(
                new PlaceData("Mendoza Capital", PlaceType.CIUDAD, "5500"),
                new PlaceData("Godoy Cruz", PlaceType.CIUDAD, "5501"),
                new PlaceData("Guaymallén", PlaceType.CIUDAD, "5519"),
                new PlaceData("Las Heras", PlaceType.CIUDAD, "5539"),
                new PlaceData("Maipú", PlaceType.CIUDAD, "5515"),
                new PlaceData("San Martín", PlaceType.CIUDAD, "5570"),
                new PlaceData("San Rafael", PlaceType.CIUDAD, "5600")
        );

        savePlaces(places, mendoza);

        log.info("✓ Created {} places in Mendoza", places.size());
    }

    private void savePlaces(List<PlaceData> placesData, Province province) {
        for (PlaceData data : placesData) {
            Place place = Place.create(
                    data.name(),
                    data.type(),
                    province.getProvinceId(),
                    data.postalCode()
            );

            placeRepository.save(place);
            log.info("    ✓ {} - {}", data.name(), data.postalCode());
        }
    }

    private void logStatistics() {
        long totalCountries = countryRepository.count();
        long totalProvinces = provinceRepository.findAll().size();
        long totalPlaces = placeRepository.findAll().size();
        long citiesCount = placeRepository.countByType(PlaceType.CIUDAD);
        long localitiesCount = placeRepository.countByType(PlaceType.LOCALIDAD);

        log.info("Geography Statistics:");
        log.info("  - Countries: {}", totalCountries);
        log.info("  - Provinces: {}", totalProvinces);
        log.info("  - Places: {}", totalPlaces);
        log.info("    - Cities: {}", citiesCount);
        log.info("    - Localities: {}", localitiesCount);
    }

    /**
     * Record helper para datos de lugares
     */
    private record PlaceData(
            String name,
            PlaceType type,
            String postalCode
    ) {}
}