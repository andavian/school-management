package org.school.management.students.records.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record DocumentTypeId(UUID value) {

    public DocumentTypeId {
        Objects.requireNonNull(value, "DocumentTypeId cannot be null");
    }

    public static DocumentTypeId of(UUID value) {
        return new DocumentTypeId(value);
    }

    // AGREGADO: alias from() según patrón estándar del proyecto
    public static DocumentTypeId from(UUID value) {
        return new DocumentTypeId(value);
    }

    public static DocumentTypeId of(String value) {
        try {
            return new DocumentTypeId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DocumentTypeId format: " + value, e);
        }
    }

    // AGREGADO: alias from(String) según patrón estándar del proyecto
    public static DocumentTypeId from(String value) {
        return of(value);
    }

    public static DocumentTypeId generate() {
        return new DocumentTypeId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}