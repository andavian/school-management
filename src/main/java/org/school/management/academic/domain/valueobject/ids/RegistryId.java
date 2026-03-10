package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record RegistryId(UUID value) {

    public RegistryId {
        if (value == null) throw new IllegalArgumentException("RegistryId cannot be null");
    }

    public static RegistryId of(UUID value) {
        return new RegistryId(value);
    }

    public static RegistryId generate() {
        return new RegistryId(UUID.randomUUID());
    }

    public static RegistryId from(UUID uuid) {
        return new RegistryId(uuid);
    }

    public static RegistryId from(String id) {
        try {
            return new RegistryId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RegistryId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
