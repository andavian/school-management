package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class EvaluationTypeId {
    UUID value;

    private EvaluationTypeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static EvaluationTypeId generate() {
        return new EvaluationTypeId(UUID.randomUUID());
    }

    public static EvaluationTypeId from(String id) {
        try {
            return new EvaluationTypeId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static EvaluationTypeId from(UUID uuid) {
        return new EvaluationTypeId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

