package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class SubjectId {
    UUID value;

    public SubjectId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static SubjectId generate() {
        return new SubjectId(UUID.randomUUID());
    }

    public static SubjectId from(String id) {
        try {
            return new SubjectId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static SubjectId from(UUID uuid) {
        return new SubjectId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

