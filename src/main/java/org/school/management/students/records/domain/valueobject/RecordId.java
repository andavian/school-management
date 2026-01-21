package org.school.management.students.records.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record RecordId(UUID value) {
    public RecordId {
        Objects.requireNonNull(value, "RecordId cannot be null");
    }

    public static RecordId of(UUID value) {
        return new RecordId(value);
    }

    public static RecordId of(String value) {
        try {
            return new RecordId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid RecordId format: " + value, e);
        }
    }

    public static RecordId generate() {
        return new RecordId(UUID.randomUUID());
    }
}