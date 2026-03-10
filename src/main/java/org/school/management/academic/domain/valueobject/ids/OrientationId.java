package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record OrientationId(UUID value) {

    public OrientationId {
        if (value == null) throw new IllegalArgumentException("OrientationId cannot be null");
    }

    public static OrientationId of(UUID value) {
        return new OrientationId(value);
    }

    public static OrientationId generate() {
        return new OrientationId(UUID.randomUUID());
    }

    public static OrientationId from(UUID uuid) {
        return new OrientationId(uuid);
    }

    public static OrientationId from(String id) {
        try {
            return new OrientationId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid OrientationId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
