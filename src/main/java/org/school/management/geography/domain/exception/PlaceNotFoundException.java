package org.school.management.geography.domain.exception;

import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.geography.domain.valueobject.ProvinceId;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(PlaceId placeId) {
        super("Place not found with id: " + placeId);
    }

    public PlaceNotFoundException(String name, ProvinceId provinceId) {
        super("Place not found: " + name + " in province: " + provinceId);
    }
}
