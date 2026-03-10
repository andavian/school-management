package org.school.management.shared.geography.domain.valueobject;

import java.util.UUID;

public record CountryId(UUID value) {

    public CountryId {
        if (value == null) {
            throw new IllegalArgumentException("CountryId cannot be null");
        }
    }

    public static CountryId of(UUID value) {
        return new CountryId(value);
    }

    public static CountryId generate() {
        return new CountryId(UUID.randomUUID());
    }

    public static CountryId from(String id) {
        try {
            return new CountryId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CountryId format: " + id);
        }
    }

    public static CountryId from(UUID uuid) {
        return new CountryId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}