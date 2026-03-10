package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record CourseId(UUID value) {

    public CourseId {
        if (value == null) throw new IllegalArgumentException("CourseId cannot be null");
    }

    public static CourseId of(UUID value) {
        return new CourseId(value);
    }

    public static CourseId generate() {
        return new CourseId(UUID.randomUUID());
    }

    public static CourseId from(UUID uuid) {
        return new CourseId(uuid);
    }

    public static CourseId from(String id) {
        try {
            return new CourseId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CourseId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
