package org.school.management.geography.domain.exception;

import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.ProvinceId;

public class ProvinceNotFoundException extends RuntimeException {
    public ProvinceNotFoundException(ProvinceId provinceId) {
        super("Province not found with id: " + provinceId);
    }

    public ProvinceNotFoundException(String name, CountryId countryId) {
        super("Province not found: " + name + " in country: " + countryId);
    }
}