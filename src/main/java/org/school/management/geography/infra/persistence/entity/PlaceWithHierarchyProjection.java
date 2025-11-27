package org.school.management.geography.infra.persistence.entity;

import org.school.management.geography.infra.persistence.entity.PlaceEntity.PlaceTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PlaceWithHierarchyProjection {

    // Place data
    private UUID placeId;
    private String placeName;
    private String placeType;
    private String postalCode;
    private LocalDateTime placeCreatedAt;

    // Province data
    private UUID provinceId;
    private String provinceName;
    private String provinceCode;

    // Country data
    private UUID countryId;
    private String countryName;
    private String countryIsoCode;
    private String countryPhoneCode;

    /**
     * Constructor para queries JPQL
     */
    @Builder
    public PlaceWithHierarchyProjection(
            UUID placeId, String placeName, PlaceTypeEnum placeType, String postalCode,
            UUID provinceId, String provinceName, String provinceCode,
            UUID countryId, String countryName, String countryIsoCode
    ) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeType = placeType != null ? placeType.name() : null;
        this.postalCode = postalCode;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryIsoCode = countryIsoCode;
    }
}
