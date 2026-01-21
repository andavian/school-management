package org.school.management.students.health.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value                                     // Inmutable automáticamente
@Builder(access = AccessLevel.PRIVATE)     // Builder privado
public class HealthRecordId {
    UUID value;

    private HealthRecordId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static HealthRecordId generate() {
        return new HealthRecordId(UUID.randomUUID());
    }

    public static HealthRecordId from(String id) {
        try {
            return new HealthRecordId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static HealthRecordId from(UUID uuid) {
        return new HealthRecordId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

