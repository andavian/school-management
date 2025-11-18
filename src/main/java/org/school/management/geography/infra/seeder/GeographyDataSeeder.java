package org.school.management.geography.infra.seeder;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeographyDataSeeder implements ApplicationRunner {

    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final PlaceRepository placeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Seeding geography data...");

        // Crear Argentina
        Country argentina = Country.create("Argentina", "ARG", "+54");
        argentina = countryRepository.save(argentina);

        // Crear Córdoba
        Province cordoba = Province.create("Córdoba", "CBA", argentina.getCountryId());
        cordoba = provinceRepository.save(cordoba);

        // Crear ciudades principales de Córdoba
        List<String> cities = List.of(
                "Córdoba Capital", "Villa María", "Río Cuarto",
                "San Francisco", "Alta Gracia", "Bell Ville"
        );

        for (String city : cities) {
            Place place = Place.create(city, "CIUDAD", cordoba.getProvinceId());
            placeRepository.save(place);
        }

        log.info("Geography data seeded successfully");
    }
}