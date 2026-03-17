package org.school.management.grades.domain.valueobject;

import java.util.UUID;

public record EvaluationId(UUID value) {

    public EvaluationId {
        if (value == null) throw new IllegalArgumentException("EvaluationId cannot be null");
    }

    public static EvaluationId of(UUID value) {
        return new EvaluationId(value);
    }

    public static EvaluationId generate() {
        return new EvaluationId(UUID.randomUUID());
    }

    public static EvaluationId from(UUID uuid) {
        return new EvaluationId(uuid);
    }

    public static EvaluationId from(String id) {
        try {
            return new EvaluationId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EvaluationId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
