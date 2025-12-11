package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class EvaluationId {
    UUID value;

    private EvaluationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static EvaluationId generate() {
        return new EvaluationId(UUID.randomUUID());
    }

    public static EvaluationId from(String id) {
        try {
            return new EvaluationId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static EvaluationId from(UUID uuid) {
        return new EvaluationId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

