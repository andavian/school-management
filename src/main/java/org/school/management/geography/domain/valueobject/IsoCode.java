package org.school.management.geography.domain.valueobject;

import lombok.Value;

@Value
public class IsoCode {
    String value;

    public static IsoCode of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISO code cannot be null or empty");
        }

        String normalized = value.trim().toUpperCase();

        if (normalized.length() != 3) {
            throw new IllegalArgumentException("ISO code must be exactly 3 characters");
        }

        if (!normalized.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("ISO code must contain only uppercase letters");
        }

        return new IsoCode(normalized);
    }

    @Override
    public String toString() {
        return value;
    }
}

