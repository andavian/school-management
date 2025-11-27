package org.school.management.geography.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value                                     // Inmutable automáticamente
@Builder(access = AccessLevel.PRIVATE)     // Builder privado
public class PlaceId {
    UUID value;

    public static PlaceId of (UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new PlaceId(value);
    }

    public static PlaceId generate() {
        return new PlaceId(UUID.randomUUID());
    }

    public static PlaceId from(String id) {
        try {
            return new PlaceId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static PlaceId from(UUID uuid) {
        return new PlaceId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

