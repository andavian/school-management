package org.school.management.auth.domain.valueobject;

import java.util.UUID;

public record RoleId(UUID value) {

    public RoleId {
        if (value == null) throw new IllegalArgumentException("RoleId cannot be null");
    }

    public static RoleId of(UUID value) {
        return new RoleId(value);
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID());
    }

    public static RoleId from(UUID uuid) {
        return new RoleId(uuid);
    }

    public static RoleId from(String id) {
        try {
            return new RoleId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RoleId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
