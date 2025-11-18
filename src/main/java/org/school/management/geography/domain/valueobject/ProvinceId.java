package org.school.management.geography.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value                                     // Inmutable automáticamente
@Builder(access = AccessLevel.PRIVATE)     // Builder privado
public class ProvinceId {
    UUID value;

    private ProvinceId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static ProvinceId generate() {
        return new ProvinceId(UUID.randomUUID());
    }

    public static ProvinceId from(String id) {
        try {
            return new ProvinceId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static ProvinceId from(UUID uuid) {
        return new ProvinceId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

