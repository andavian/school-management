package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.response.GeographyStatisticsResponse;
import org.school.management.geography.domain.repository.CountryRepository;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.geography.domain.valueobject.PlaceType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetGeographyStatisticsUseCase {

    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final PlaceRepository placeRepository;

    public GeographyStatisticsResponse execute() {
        log.info("Getting geography statistics");

        long totalCountries = countryRepository.count();
        long totalProvinces = provinceRepository.findAll().size();
        long totalPlaces = placeRepository.findAll().size();
        long citiesCount = placeRepository.countByType(PlaceType.CIUDAD);
        long localitiesCount = placeRepository.countByType(PlaceType.LOCALIDAD);

        return GeographyStatisticsResponse.builder()
                .totalCountries(totalCountries)
                .totalProvinces(totalProvinces)
                .totalPlaces(totalPlaces)
                .citiesCount(citiesCount)
                .localitiesCount(localitiesCount)
                .build();
    }
}
