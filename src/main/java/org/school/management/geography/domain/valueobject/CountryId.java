package org.school.management.geography.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value                                     // Inmutable automáticamente
@Builder(access = AccessLevel.PRIVATE)     // Builder privado
public class CountryId {
    UUID value;

    public static CountryId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new CountryId(value);
    }

    public static CountryId generate() {
        return new CountryId(UUID.randomUUID());
    }

    public static CountryId from(String id) {
        try {
            return new CountryId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static CountryId from(UUID uuid) {
        return new CountryId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

