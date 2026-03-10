package org.school.management.shared.geography.domain.valueobject;

import java.util.UUID;

public record ProvinceId(UUID value) {

    public ProvinceId {
        if (value == null) {
            throw new IllegalArgumentException("ProvinceId cannot be null");
        }
    }

    public static ProvinceId of(UUID value) {
        return new ProvinceId(value);
    }

    public static ProvinceId generate() {
        return new ProvinceId(UUID.randomUUID());
    }

    public static ProvinceId from(String id) {
        try {
            return new ProvinceId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ProvinceId format: " + id);
        }
    }

    public static ProvinceId from(UUID uuid) {
        return new ProvinceId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}