package org.school.management.resources.domain.valueobject;

import java.util.UUID;

public record UnitId(UUID value) {
    public UnitId {
        if (value == null) throw new IllegalArgumentException("UnitId cannot be null");
    }

    public static UnitId of(UUID value) { return new UnitId(value); }
    public static UnitId generate() { return new UnitId(UUID.randomUUID()); }
    public static UnitId from(UUID uuid) { return new UnitId(uuid); }
    public static UnitId from(String id) {
        try { return new UnitId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UnitId format: " + id);
        }
    }
}