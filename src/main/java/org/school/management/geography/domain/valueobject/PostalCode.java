package org.school.management.geography.domain.valueobject;

import java.util.Optional;

public record PostalCode(String value) {

    public PostalCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PostalCode cannot be null or blank");
        }

        value = value.trim().toUpperCase();

        if (!value.matches("^[0-9A-Z]{4,10}$")) {
            throw new IllegalArgumentException("Invalid postal code format: " + value);
        }
    }


    public static PostalCode of(String value) {
        return new PostalCode(value);
    }


    public static Optional<PostalCode> ofNullable(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new PostalCode(value));
    }

    @Override
    public String toString() {
        return value;
    }
}