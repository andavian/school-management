package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class AcademicYearId {
    UUID value;

    public AcademicYearId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static AcademicYearId generate() {
        return new AcademicYearId(UUID.randomUUID());
    }

    public static AcademicYearId from(String id) {
        try {
            return new AcademicYearId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static AcademicYearId from(UUID uuid) {
        return new AcademicYearId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

