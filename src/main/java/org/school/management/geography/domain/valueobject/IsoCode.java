package org.school.management.geography.domain.valueobject;

public record IsoCode(String value) {

    public IsoCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISO code cannot be null or empty");
        }

        value = value.trim().toUpperCase();

        if (value.length() != 3) {
            throw new IllegalArgumentException("ISO code must be exactly 3 characters");
        }

        if (!value.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("ISO code must contain only uppercase letters");
        }
    }

    public static IsoCode of(String value) {
        return new IsoCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}