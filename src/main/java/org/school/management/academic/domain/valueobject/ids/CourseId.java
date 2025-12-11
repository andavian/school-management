package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class CourseId {
    UUID value;

    private CourseId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static CourseId generate() {
        return new CourseId(UUID.randomUUID());
    }

    public static CourseId from(String id) {
        try {
            return new CourseId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static CourseId from(UUID uuid) {
        return new CourseId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

