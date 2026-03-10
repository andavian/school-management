package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record GradeLevelId(UUID value) {

    public GradeLevelId {
        if (value == null) throw new IllegalArgumentException("GradeLevelId cannot be null");
    }

    public static GradeLevelId of(UUID value) {
        return new GradeLevelId(value);
    }

    public static GradeLevelId generate() {
        return new GradeLevelId(UUID.randomUUID());
    }

    public static GradeLevelId from(UUID uuid) {
        return new GradeLevelId(uuid);
    }

    public static GradeLevelId from(String id) {
        try {
            return new GradeLevelId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid GradeLevelId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
