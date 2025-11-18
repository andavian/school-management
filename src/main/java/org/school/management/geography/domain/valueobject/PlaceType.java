package org.school.management.geography.domain.valueobject;

import lombok.Getter;


public enum PlaceType {
    CIUDAD("Ciudad"),
    LOCALIDAD("Localidad"),
    MUNICIPIO("Municipio"),
    PARAJE("Paraje"),
    COMUNA("Comuna");

    private final String displayName;

    PlaceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PlaceType fromString(String value) {
        if (value == null || value.isBlank()) {
            return LOCALIDAD; // Default
        }

        try {
            return PlaceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid place type: " + value);
        }
    }
}
