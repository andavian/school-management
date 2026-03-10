
package org.school.management.academic.domain.valueobject;

public record Division(String value) {

    public Division {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Division cannot be null or empty");
        }
        value = value.trim().toUpperCase();
        if (!value.matches("^[A-Z]{1,2}$")) {
            throw new IllegalArgumentException("Division must be 1-2 uppercase letters");
        }
    }

    public static Division of(String value) {
        return new Division(value);
    }
}

