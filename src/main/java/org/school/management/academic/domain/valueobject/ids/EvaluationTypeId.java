package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record EvaluationTypeId(UUID value) {

    public EvaluationTypeId {
        if (value == null) throw new IllegalArgumentException("EvaluationTypeId cannot be null");
    }

    public static EvaluationTypeId of(UUID value) {
        return new EvaluationTypeId(value);
    }

    public static EvaluationTypeId generate() {
        return new EvaluationTypeId(UUID.randomUUID());
    }

    public static EvaluationTypeId from(UUID uuid) {
        return new EvaluationTypeId(uuid);
    }

    public static EvaluationTypeId from(String id) {
        try {
            return new EvaluationTypeId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EvaluationTypeId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
