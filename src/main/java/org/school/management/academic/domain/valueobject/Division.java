package org.school.management.academic.domain.valueobject;

import lombok.Value;

@Value
public class Division {
    String value;

    public static Division of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Division cannot be null or empty");
        }

        String normalized = value.trim().toUpperCase();

        if (!normalized.matches("^[A-Z]{1,2}$")) {
            throw new IllegalArgumentException("Division must be 1-2 uppercase letters");
        }

        return new Division(normalized);
    }
}