package org.school.management.academic.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value                                     // Inmutable automáticamente
@Builder(access = AccessLevel.PRIVATE)     // Builder privado
public class StudentId {
    UUID value;

    private StudentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static StudentId generate() {
        return new StudentId(UUID.randomUUID());
    }

    public static StudentId from(String id) {
        try {
            return new StudentId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static StudentId from(UUID uuid) {
        return new StudentId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

