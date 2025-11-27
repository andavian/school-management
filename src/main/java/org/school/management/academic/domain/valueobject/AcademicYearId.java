package org.school.management.academic.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class AcademicYearId {
    UUID value;

    private AcademicYearId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static OrientationId generate() {
        return new OrientationId(UUID.randomUUID());
    }

    public static OrientationId from(String id) {
        try {
            return new OrientationId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static OrientationId from(UUID uuid) {
        return new OrientationId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

