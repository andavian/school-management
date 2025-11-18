package org.school.management.geography.domain.exception;

import org.school.management.geography.domain.valueobject.CountryId;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(CountryId countryId) {
        super("Country not found with id: " + countryId);
    }

    public CountryNotFoundException(String isoCode) {
        super("Country not found with ISO code: " + isoCode);
    }
}