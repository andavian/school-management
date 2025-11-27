package org.school.management.geography.infra.persistence.mappers;


import org.mapstruct.*;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.valueobject.*;
import org.school.management.geography.infra.persistence.entity.CountryEntity;
import org.school.management.geography.infra.persistence.entity.PlaceEntity;
import org.school.management.geography.infra.persistence.entity.PlaceWithHierarchyProjection;
import org.school.management.geography.infra.persistence.entity.ProvinceEntity;

import java.util.UUID;

/**
 * MapStruct Mapper para convertir entre Domain Models y JPA Entities
 * Capa de persistencia del Geography Module
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GeographyPersistenceMapper {

    // ========================================================================
    // COUNTRY MAPPINGS
    // ========================================================================

    /**
     * Convertir Country domain model a CountryEntity
     */
    @Mapping(target = "countryId", source = "countryId.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "isoCode", source = "isoCode.value")
    @Mapping(target = "phoneCode", source = "phoneCode.value")
    CountryEntity toCountryEntity(Country country);

    /**
     * Convertir CountryEntity a Country domain model
     */
    @Mapping(target = "countryId", expression = "java(mapCountryId(entity.getCountryId()))")
    @Mapping(target = "name", expression = "java(mapGeographicName(entity.getName()))")
    @Mapping(target = "isoCode", expression = "java(mapIsoCode(entity.getIsoCode()))")
    @Mapping(target = "phoneCode", expression = "java(mapPhoneCode(entity.getPhoneCode()))")
    Country toCountryDomain(CountryEntity entity);

    // ========================================================================
    // PROVINCE MAPPINGS
    // ========================================================================

    /**
     * Convertir Province domain model a ProvinceEntity
     */
    @Mapping(target = "provinceId", source = "provinceId.value")
    @Mapping(target = "countryId", source = "countryId.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "code", source = "code.value")
    @Mapping(target = "country", ignore = true)
    ProvinceEntity toProvinceEntity(Province province);

    /**
     * Convertir ProvinceEntity a Province domain model
     */
    @Mapping(target = "provinceId", expression = "java(mapProvinceId(entity.getProvinceId()))")
    @Mapping(target = "countryId", expression = "java(mapCountryId(entity.getCountryId()))")
    @Mapping(target = "name", expression = "java(mapGeographicName(entity.getName()))")
    @Mapping(target = "code", expression = "java(mapProvinceCode(entity.getCode()))")
    Province toProvinceDomain(ProvinceEntity entity);

    // ========================================================================
    // PLACE MAPPINGS
    // ========================================================================

    /**
     * Convertir Place domain model a PlaceEntity
     */
    @Mapping(target = "placeId", source = "placeId.value")
    @Mapping(target = "provinceId", source = "provinceId.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "type", expression = "java(toPlaceTypeEntity(place.getType()))")
    @Mapping(target = "postalCode", source = "postalCode.value")
    @Mapping(target = "province", ignore = true)
    PlaceEntity toPlaceEntity(Place place);

    /**
     * Convertir PlaceEntity a Place domain model
     */
    @Mapping(target = "placeId", expression = "java(mapPlaceId(entity.getPlaceId()))")
    @Mapping(target = "provinceId", expression = "java(mapProvinceId(entity.getProvinceId()))")
    @Mapping(target = "name", expression = "java(mapGeographicName(entity.getName()))")
    @Mapping(target = "type", expression = "java(toPlaceTypeDomain(entity.getType()))")
    @Mapping(target = "postalCode", expression = "java(mapPostalCode(entity.getPostalCode()))")
    Place toPlaceDomain(PlaceEntity entity);

    // ========================================================================
    // PLACE WITH HIERARCHY MAPPING
    // ========================================================================

    /**
     * Convertir PlaceWithHierarchyProjection a PlaceWithHierarchy domain model
     */
    default PlaceWithHierarchy toPlaceWithHierarchy(PlaceWithHierarchyProjection projection) {
        // Crear Place
        Place place = Place.builder()
                .placeId(PlaceId.of(projection.getPlaceId()))
                .provinceId(ProvinceId.of(projection.getProvinceId()))
                .name(GeographicName.of(projection.getPlaceName()))
                .type(PlaceType.fromString(projection.getPlaceType()))
                .postalCode(projection.getPostalCode() != null ?
                        PostalCode.of(projection.getPostalCode()) : null)
                .createdAt(projection.getPlaceCreatedAt())
                .build();

        // Crear Province
        Province province = Province.builder()
                .provinceId(ProvinceId.of(projection.getProvinceId()))
                .countryId(CountryId.of(projection.getCountryId()))
                .name(GeographicName.of(projection.getProvinceName()))
                .code(projection.getProvinceCode() != null ?
                        ProvinceCode.of(projection.getProvinceCode()) : null)
                .createdAt(null)  // No disponible en projection
                .build();

        // Crear Country
        Country country = Country.builder()
                .countryId(CountryId.of(projection.getCountryId()))
                .name(GeographicName.of(projection.getCountryName()))
                .isoCode(IsoCode.of(projection.getCountryIsoCode()))
                .phoneCode(projection.getCountryPhoneCode() != null ?
                        PhoneCode.of(projection.getCountryPhoneCode()) : null)
                .createdAt(null)  // No disponible en projection
                .build();

        return PlaceWithHierarchy.builder()
                .place(place)
                .province(province)
                .country(country)
                .build();
    }

    // ========================================================================
    // ENUM CONVERSIONS
    // ========================================================================

    /**
     * Convertir PlaceType del dominio a PlaceTypeEnum de la entidad
     */
    default PlaceEntity.PlaceTypeEnum toPlaceTypeEntity(PlaceType domainType) {
        if (domainType == null) {
            return null;
        }
        return PlaceEntity.PlaceTypeEnum.valueOf(domainType.name());
    }

    /**
     * Convertir PlaceTypeEnum de la entidad a PlaceType del dominio
     */
    default PlaceType toPlaceTypeDomain(PlaceEntity.PlaceTypeEnum entityType) {
        if (entityType == null) {
            return null;
        }
        return PlaceType.valueOf(entityType.name());
    }

    // ========================================================================
    // VALUE OBJECT CONVERSIONS
    // ========================================================================

    default CountryId mapCountryId(UUID uuid) {
        return uuid != null ? CountryId.of(uuid) : null;
    }

    default ProvinceId mapProvinceId(UUID uuid) {
        return uuid != null ? ProvinceId.of(uuid) : null;
    }

    default PlaceId mapPlaceId(UUID uuid) {
        return uuid != null ? PlaceId.of(uuid) : null;
    }

    default GeographicName mapGeographicName(String name) {
        return name != null ? GeographicName.of(name) : null;
    }

    default IsoCode mapIsoCode(String code) {
        return code != null ? IsoCode.of(code) : null;
    }

    default PhoneCode mapPhoneCode(String code) {
        return code != null ? PhoneCode.of(code) : null;
    }

    default ProvinceCode mapProvinceCode(String code) {
        return code != null ? ProvinceCode.of(code) : null;
    }

    default PostalCode mapPostalCode(String code) {
        return code != null ? PostalCode.of(code) : null;
    }
}
