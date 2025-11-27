package org.school.management.geography.infra.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.*;
import org.school.management.geography.application.dto.response.*;
import org.school.management.geography.application.usecases.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller para Geography Module
 * Endpoints públicos para consulta de lugares geográficos
 */
@RestController
@RequestMapping("/api/geography")
@RequiredArgsConstructor
@Slf4j
public class GeographyController {

    private final GetPlaceByIdUseCase getPlaceByIdUseCase;
    private final SearchPlacesUseCase searchPlacesUseCase;
    private final ListPlacesByProvinceUseCase listPlacesByProvinceUseCase;
    private final ListProvincesByCountryUseCase listProvincesByCountryUseCase;
    private final SearchProvincesUseCase searchProvincesUseCase;
    private final ListCountriesUseCase listCountriesUseCase;
    private final GetCountryByIsoCodeUseCase getCountryByIsoCodeUseCase;
    private final GlobalSearchUseCase globalSearchUseCase;
    private final GetGeographyStatisticsUseCase getStatisticsUseCase;

    // ========================================================================
    // COUNTRIES
    // ========================================================================

    /**
     * Listar todos los países
     * GET /api/geography/countries
     */
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> listCountries() {
        log.info("GET /api/geography/countries");

        List<CountryResponse> countries = listCountriesUseCase.execute();

        return ResponseEntity.ok(countries);
    }

    /**
     * Obtener país por código ISO
     * GET /api/geography/countries/ARG
     */
    @GetMapping("/countries/{isoCode}")
    public ResponseEntity<CountryResponse> getCountryByIsoCode(
            @PathVariable String isoCode
    ) {
        log.info("GET /api/geography/countries/{}", isoCode);

        CountryResponse country = getCountryByIsoCodeUseCase.execute(
                new GetCountryByIsoCodeRequest(isoCode)
        );

        return ResponseEntity.ok(country);
    }

    // ========================================================================
    // PROVINCES
    // ========================================================================

    /**
     * Listar provincias de un país
     * GET /api/geography/countries/{countryId}/provinces
     */
    @GetMapping("/countries/{countryId}/provinces")
    public ResponseEntity<List<ProvinceResponse>> listProvinces(
            @PathVariable UUID countryId
    ) {
        log.info("GET /api/geography/countries/{}/provinces", countryId);

        List<ProvinceResponse> provinces = listProvincesByCountryUseCase.execute(
                new ListProvincesByCountryRequest(countryId)
        );

        return ResponseEntity.ok(provinces);
    }

    /**
     * Buscar provincias por nombre
     * GET /api/geography/provinces/search?q=Cordoba
     */
    @GetMapping("/provinces/search")
    public ResponseEntity<List<ProvinceResponse>> searchProvinces(
            @RequestParam String q
    ) {
        log.info("GET /api/geography/provinces/search?q={}", q);

        List<ProvinceResponse> provinces = searchProvincesUseCase.execute(
                new SearchProvincesRequest(q)
        );

        return ResponseEntity.ok(provinces);
    }

    // ========================================================================
    // PLACES
    // ========================================================================

    /**
     * Obtener lugar por ID con jerarquía completa
     * GET /api/geography/places/{placeId}
     */
    @GetMapping("/places/{placeId}")
    public ResponseEntity<PlaceResponse> getPlaceById(
            @PathVariable UUID placeId
    ) {
        log.info("GET /api/geography/places/{}", placeId);

        PlaceResponse place = getPlaceByIdUseCase.execute(
                new GetPlaceByIdRequest(placeId)
        );

        return ResponseEntity.ok(place);
    }

    /**
     * Listar lugares de una provincia
     * GET /api/geography/provinces/{provinceId}/places
     * GET /api/geography/provinces/{provinceId}/places?type=CIUDAD
     */
    @GetMapping("/provinces/{provinceId}/places")
    public ResponseEntity<List<PlaceSummaryResponse>> listPlaces(
            @PathVariable UUID provinceId,
            @RequestParam(required = false) String type
    ) {
        log.info("GET /api/geography/provinces/{}/places?type={}", provinceId, type);

        List<PlaceSummaryResponse> places = listPlacesByProvinceUseCase.execute(
                new ListPlacesByProvinceRequest(provinceId, type)
        );

        return ResponseEntity.ok(places);
    }

    /**
     * Buscar lugares por nombre
     * GET /api/geography/places/search?q=Alta Gracia
     * GET /api/geography/places/search?q=Cordoba&provinceId={uuid}
     */
    @GetMapping("/places/search")
    public ResponseEntity<List<PlaceResponse>> searchPlaces(
            @RequestParam String q,
            @RequestParam(required = false) UUID provinceId
    ) {
        log.info("GET /api/geography/places/search?q={}&provinceId={}", q, provinceId);

        List<PlaceResponse> places = searchPlacesUseCase.execute(
                new SearchPlacesRequest(q, provinceId)
        );

        return ResponseEntity.ok(places);
    }

    // ========================================================================
    // GLOBAL SEARCH
    // ========================================================================

    /**
     * Búsqueda global en toda la jerarquía
     * GET /api/geography/search?q=Alta&limit=10
     */
    @GetMapping("/search")
    public ResponseEntity<List<PlaceResponse>> globalSearch(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        log.info("GET /api/geography/search?q={}&limit={}", q, limit);

        List<PlaceResponse> results = globalSearchUseCase.execute(
                new GlobalSearchRequest(q, limit)
        );

        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // STATISTICS
    // ========================================================================

    /**
     * Obtener estadísticas geográficas
     * GET /api/geography/statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeographyStatisticsResponse> getStatistics() {
        log.info("GET /api/geography/statistics");

        GeographyStatisticsResponse stats = getStatisticsUseCase.execute();

        return ResponseEntity.ok(stats);
    }
}

