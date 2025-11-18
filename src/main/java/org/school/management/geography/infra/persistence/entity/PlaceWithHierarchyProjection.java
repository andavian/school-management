package org.school.management.geography.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    public PlaceWithHierarchyProjection(
            UUID placeId, String placeName, String placeType, String postalCode,
            UUID provinceId, String provinceName, String provinceCode,
            UUID countryId, String countryName, String countryIsoCode
    ) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeType = placeType;
        this.postalCode = postalCode;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryIsoCode = countryIsoCode;
    }
}
