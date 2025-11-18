package org.school.management.geography.domain.repository;

import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.valueobject.CountryId;

import java.util.List;

public interface GeographyQueryRepository {

    /**
     * Obtener jerarquía completa: País → Provincias → Lugares
     */
    List<CountryWithProvinces> getCompleteHierarchy();

    /**
     * Obtener provincias con sus lugares
     */
    List<ProvinceWithPlaces> getProvincesWithPlaces(CountryId countryId);

    /**
     * Buscar en toda la jerarquía por texto
     */
    List<PlaceWithHierarchy> globalSearch(String searchTerm);

    /**
     * Obtener estadísticas geográficas
     */
    GeographyStatistics getStatistics();

    /**
     * Value Object para país con sus provincias
     */
    record CountryWithProvinces(
            Country country,
            List<Province> provinces
    ) {}

    /**
     * Value Object para provincia con sus lugares
     */
    record ProvinceWithPlaces(
            Province province,
            List<Place> places,
            long totalPlaces
    ) {}

    /**
     * Value Object para estadísticas
     */
    record GeographyStatistics(
            long totalCountries,
            long totalProvinces,
            long totalPlaces,
            long citiesCount,
            long localitiesCount
    ) {}
}
