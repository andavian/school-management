package org.school.management.geography.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.GeographicName;
import org.school.management.geography.domain.valueobject.ProvinceCode;
import org.school.management.geography.domain.valueobject.ProvinceId;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Province {
    ProvinceId provinceId;
    CountryId countryId;
    GeographicName name;
    @With
    ProvinceCode code;
    LocalDateTime createdAt;

    /**
     * Factory method para crear una nueva provincia
     */
    public static Province create(String name, String code, CountryId countryId) {
        if (countryId == null) {
            throw new IllegalArgumentException("Country is required for province");
        }

        return Province.builder()
                .provinceId(ProvinceId.generate())
                .countryId(countryId)
                .name(GeographicName.of(name))
                .code(code != null ? ProvinceCode.of(code) : null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Actualizar código de provincia
     */
    public Province updateCode(String newCode) {
        return this.withCode(newCode != null ? ProvinceCode.of(newCode) : null);
    }

    /**
     * Verificar si pertenece a un país específico
     */
    public boolean belongsToCountry(CountryId countryId) {
        return this.countryId.equals(countryId);
    }

    /**
     * Obtener nombre como String
     */
    public String getNameAsString() {
        return name.getValue();
    }

    /**
     * Obtener código como String
     */
    public String getCodeAsString() {
        return code != null ? code.getValue() : null;
    }
}
