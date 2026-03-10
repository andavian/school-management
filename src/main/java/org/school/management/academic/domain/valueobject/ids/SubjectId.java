package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record SubjectId(UUID value) {

    public SubjectId {
        if (value == null) throw new IllegalArgumentException("SubjectId cannot be null");
    }

    public static SubjectId of(UUID value) {
        return new SubjectId(value);
    }

    public static SubjectId generate() {
        return new SubjectId(UUID.randomUUID());
    }

    public static SubjectId from(UUID uuid) {
        return new SubjectId(uuid);
    }

    public static SubjectId from(String id) {
        try {
            return new SubjectId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid SubjectId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
