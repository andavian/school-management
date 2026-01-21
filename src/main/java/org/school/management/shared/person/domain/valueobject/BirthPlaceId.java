package org.school.management.shared.person.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record BirthPlaceId(UUID value) {
    public BirthPlaceId {
        Objects.requireNonNull(value, "BirthPlaceId cannot be null");
    }

    public static BirthPlaceId of(UUID value) {
        return new BirthPlaceId(value);
    }

    public static BirthPlaceId of(String value) {
        try {
            return new BirthPlaceId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + value, e);
        }
    }

    public static BirthPlaceId generate() {
        return new BirthPlaceId(UUID.randomUUID());
    }
}