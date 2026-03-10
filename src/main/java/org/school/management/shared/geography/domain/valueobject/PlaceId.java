package org.school.management.shared.geography.domain.valueobject;

import java.util.UUID;

/**
 * Value Object: identificador de un lugar geográfico (Place).
 * Compartido entre bounded contexts via Shared Kernel.
 */
public record PlaceId(UUID value) {

    // Constructor compacto — validación
    public PlaceId {
        if (value == null) {
            throw new IllegalArgumentException("PlaceId cannot be null");
        }
    }

    public static PlaceId of(UUID value) {
        return new PlaceId(value);
    }

    public static PlaceId generate() {
        return new PlaceId(UUID.randomUUID());
    }

    public static PlaceId from(String id) {
        try {
            return new PlaceId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PlaceId format: " + id);
        }
    }

    public static PlaceId from(UUID uuid) {
        return new PlaceId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}