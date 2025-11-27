package org.school.management.geography.application.mappers;

import org.mapstruct.*;
import org.school.management.geography.application.dto.response.CountryResponse;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.dto.response.PlaceSummaryResponse;
import org.school.management.geography.application.dto.response.ProvinceResponse;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.model.Province;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GeographyApplicationMapper {

    // ========================================================================
    // COUNTRY MAPPINGS
    // ========================================================================

    @Mapping(target = "countryId", source = "countryId.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "isoCode", source = "isoCode.value")
    @Mapping(target = "phoneCode", source = "phoneCode.value")
    CountryResponse toCountryResponse(Country country);

    // ========================================================================
    // PROVINCE MAPPINGS
    // ========================================================================

    @Mapping(target = "provinceId", source = "provinceId.value")
    @Mapping(target = "countryId", source = "countryId.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "code", source = "code.value")
    @Mapping(target = "countryName", ignore = true)  // Se setea manualmente si es necesario
    ProvinceResponse toProvinceResponse(Province province);

    // ========================================================================
    // PLACE MAPPINGS
    // ========================================================================

    /**
     * Mapear Place a PlaceSummaryResponse (sin jerarquía)
     */
    @Mapping(target = "placeId", source = "placeId.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "type", expression = "java(place.getTypeAsString())")
    @Mapping(target = "postalCode", source = "postalCode.value")
    PlaceSummaryResponse toPlaceSummaryResponse(Place place);

    /**
     * Mapear PlaceWithHierarchy a PlaceResponse completo
     */
    @Mapping(target = "placeId", source = "place.placeId.value")
    @Mapping(target = "provinceId", source = "place.provinceId.value")
    @Mapping(target = "name", source = "place.name.value")
    @Mapping(target = "type", expression = "java(placeWithHierarchy.getPlace().getTypeAsString())")
    @Mapping(target = "typeDisplayName", expression = "java(placeWithHierarchy.getPlace().getTypeDisplayName())")
    @Mapping(target = "postalCode", source = "place.postalCode.value")
    @Mapping(target = "createdAt", source = "place.createdAt")
    @Mapping(target = "provinceName", source = "province.name.value")
    @Mapping(target = "provinceCode", source = "province.code.value")
    @Mapping(target = "countryName", source = "country.name.value")
    @Mapping(target = "countryIsoCode", source = "country.isoCode.value")
    @Mapping(target = "fullAddress", expression = "java(placeWithHierarchy.getFullAddress())")
    @Mapping(target = "fullDescription", expression = "java(placeWithHierarchy.getFullDescription())")
    PlaceResponse toPlaceResponse(PlaceWithHierarchy placeWithHierarchy);
}