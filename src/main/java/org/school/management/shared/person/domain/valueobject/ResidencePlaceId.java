// src/main/java/org/school/management/shared/person/domain/valueobject/BirthPlaceId.java
package org.school.management.shared.person.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record ResidencePlaceId(UUID value) {
    public ResidencePlaceId {
        Objects.requireNonNull(value, "ResidencePlaceId cannot be null");
    }

    public static ResidencePlaceId of(UUID value) {
        return new ResidencePlaceId(value);
    }

    public static ResidencePlaceId of(String value) {
        try {
            return new ResidencePlaceId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + value, e);
        }
    }

    public static ResidencePlaceId generate() {
        return new ResidencePlaceId(UUID.randomUUID());
    }
}