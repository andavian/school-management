package org.school.management.resources.domain.valueobject;

import java.util.UUID;

public record ResourceId(UUID value) {
    public ResourceId {
        if (value == null) throw new IllegalArgumentException("ResourceId cannot be null");
    }
    public static ResourceId of(UUID value) { return new ResourceId(value); }
    public static ResourceId generate() { return new ResourceId(UUID.randomUUID()); }
    public static ResourceId from(UUID uuid) { return of(uuid); }
    public static ResourceId from(String id) {
        try { return new ResourceId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ResourceId format: " + id);
        }
    }
    public String asString() { return value.toString(); }
}