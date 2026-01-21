package org.school.management.students.records.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record DocumentId(UUID value) {
    public DocumentId {
        Objects.requireNonNull(value, "DocumentId cannot be null");
    }

    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }

    public static DocumentId of(String value) {
        try {
            return new DocumentId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DocumentId format: " + value, e);
        }
    }

    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }
}