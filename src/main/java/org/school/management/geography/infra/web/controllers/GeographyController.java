package org.school.management.geography.infra.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.*;
import org.school.management.geography.application.dto.response.*;
import org.school.management.geography.application.usecases.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/geography")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Geography",
        description = "Endpoints públicos para consulta de países, provincias y localidades"
)
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

    // ============================================================
    // COUNTRIES
    // ============================================================

    @Operation(
            summary = "Listar países",
            description = "Retorna todos los países registrados en el sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de países",
            content = @Content(schema = @Schema(implementation = CountryResponse.class))
    )
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> listCountries() {
        log.info("GET /api/geography/countries");
        return ResponseEntity.ok(listCountriesUseCase.execute());
    }

    @Operation(
            summary = "Obtener país por código ISO",
            description = "Retorna la información del país correspondiente al código ISO proporcionado."
    )
    @GetMapping("/countries/{isoCode}")
    public ResponseEntity<CountryResponse> getCountryByIsoCode(
            @Parameter(description = "Código ISO del país", example = "ARG")
            @PathVariable String isoCode
    ) {
        log.info("GET /api/geography/countries/{}", isoCode);
        return ResponseEntity.ok(
                getCountryByIsoCodeUseCase.execute(new GetCountryByIsoCodeRequest(isoCode))
        );
    }

    // ============================================================
    // PROVINCES
    // ============================================================

    @Operation(
            summary = "Listar provincias de un país",
            description = "Retorna todas las provincias pertenecientes al país especificado."
    )
    @GetMapping("/countries/{countryId}/provinces")
    public ResponseEntity<List<ProvinceResponse>> listProvinces(
            @Parameter(description = "ID del país", example = "uuid")
            @PathVariable UUID countryId
    ) {
        log.info("GET /api/geography/countries/{}/provinces", countryId);
        return ResponseEntity.ok(
                listProvincesByCountryUseCase.execute(new ListProvincesByCountryRequest(countryId))
        );
    }

    @Operation(
            summary = "Buscar provincias por nombre",
            description = "Permite buscar provincias por coincidencia parcial del nombre."
    )
    @GetMapping("/provinces/search")
    public ResponseEntity<List<ProvinceResponse>> searchProvinces(
            @Parameter(description = "Texto de búsqueda", example = "Córdoba")
            @RequestParam String q
    ) {
        log.info("GET /api/geography/provinces/search?q={}", q);
        return ResponseEntity.ok(
                searchProvincesUseCase.execute(new SearchProvincesRequest(q))
        );
    }

    // ============================================================
    // PLACES
    // ============================================================

    @Operation(
            summary = "Obtener localidad por ID",
            description = "Retorna la localidad y su jerarquía completa (provincia y país)."
    )
    @GetMapping("/places/{placeId}")
    public ResponseEntity<PlaceResponse> getPlaceById(
            @Parameter(description = "ID de la localidad", example = "uuid")
            @PathVariable UUID placeId
    ) {
        log.info("GET /api/geography/places/{}", placeId);
        return ResponseEntity.ok(
                getPlaceByIdUseCase.execute(new GetPlaceByIdRequest(placeId))
        );
    }

    @Operation(
            summary = "Listar localidades de una provincia",
            description = "Retorna todas las localidades pertenecientes a una provincia. Se puede filtrar por tipo."
    )
    @GetMapping("/provinces/{provinceId}/places")
    public ResponseEntity<List<PlaceSummaryResponse>> listPlaces(
            @Parameter(description = "ID de la provincia", example = "uuid")
            @PathVariable UUID provinceId,
            @Parameter(description = "Tipo opcional de localidad", example = "CIUDAD")
            @RequestParam(required = false) String type
    ) {
        log.info("GET /api/geography/provinces/{}/places?type={}", provinceId, type);
        return ResponseEntity.ok(
                listPlacesByProvinceUseCase.execute(
                        new ListPlacesByProvinceRequest(provinceId, type)
                )
        );
    }

    @Operation(
            summary = "Buscar localidades",
            description = "Permite buscar localidades por nombre, con filtro opcional por provincia."
    )
    @GetMapping("/places/search")
    public ResponseEntity<List<PlaceResponse>> searchPlaces(
            @Parameter(description = "Texto de búsqueda", example = "Alta Gracia")
            @RequestParam String q,
            @Parameter(description = "ID opcional de provincia para filtrar", example = "uuid")
            @RequestParam(required = false) UUID provinceId
    ) {
        log.info("GET /api/geography/places/search?q={}&provinceId={}", q, provinceId);
        return ResponseEntity.ok(
                searchPlacesUseCase.execute(new SearchPlacesRequest(q, provinceId))
        );
    }

    // ============================================================
    // GLOBAL SEARCH
    // ============================================================

    @Operation(
            summary = "Búsqueda global",
            description = "Realiza una búsqueda en toda la jerarquía geográfica: países, provincias y localidades."
    )
    @GetMapping("/search")
    public ResponseEntity<List<PlaceResponse>> globalSearch(
            @Parameter(description = "Texto de búsqueda general")
            @RequestParam String q,
            @Parameter(description = "Límite de resultados", example = "50")
            @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        log.info("GET /api/geography/search?q={}&limit={}", q, limit);
        return ResponseEntity.ok(
                globalSearchUseCase.execute(new GlobalSearchRequest(q, limit))
        );
    }

    // ============================================================
    // STATISTICS (ADMIN)
    // ============================================================

    @Operation(
            summary = "Obtener estadísticas geográficas",
            description = "Solo para administradores. Retorna conteos y métricas generales del módulo de geografía."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<GeographyStatisticsResponse> getStatistics() {
        log.info("GET /api/geography/statistics");
        return ResponseEntity.ok(getStatisticsUseCase.execute());
    }
}
