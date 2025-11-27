package org.school.management.academic.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GradeLevelId {
    UUID value;

    private GradeLevelId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static GradeLevelId generate() {
        return new GradeLevelId(UUID.randomUUID());
    }

    public static GradeLevelId from(String id) {
        try {
            return new GradeLevelId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static GradeLevelId from(UUID uuid) {
        return new GradeLevelId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

