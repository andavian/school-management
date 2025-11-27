// geography/domain/model/Country.java
package org.school.management.geography.domain.model;


import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.GeographicName;
import org.school.management.geography.domain.valueobject.IsoCode;
import org.school.management.geography.domain.valueobject.PhoneCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Country {
    CountryId countryId;
    GeographicName name;
    IsoCode isoCode;
    @With
    PhoneCode phoneCode;
    LocalDateTime createdAt;

    /**
     * Factory method para crear un nuevo país
     */
    public static Country create(String name, String isoCode, String phoneCode) {
        return Country.builder()
                .countryId(CountryId.generate())
                .name(GeographicName.of(name))
                .isoCode(IsoCode.of(isoCode))
                .phoneCode(phoneCode != null ? PhoneCode.of(phoneCode) : null)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public static Country withId(UUID countryId, String name, String isoCode, String phoneCode) {
        return Country.builder()
                .countryId(CountryId.of(countryId))
                .name(GeographicName.of(name))
                .isoCode(IsoCode.of(isoCode))
                .phoneCode(phoneCode != null ? PhoneCode.of(phoneCode) : null)
                .createdAt(LocalDateTime.now())
                .build();
    }
    /**
     * Actualizar código telefónico
     */
    public Country updatePhoneCode(String newPhoneCode) {
        if (newPhoneCode == null) {
            throw new IllegalArgumentException("Phone code cannot be null");
        }
        return this.withPhoneCode(PhoneCode.of(newPhoneCode));
    }

    /**
     * Obtener nombre del país como String
     */
    public String getNameAsString() {
        return name.getValue();
    }

    /**
     * Obtener código ISO como String
     */
    public String getIsoCodeAsString() {
        return isoCode.getValue();
    }

    /**
     * Obtener código telefónico como String (con +)
     */
    public String getPhoneCodeAsString() {
        return phoneCode != null ? phoneCode.getValue() : null;
    }
}
