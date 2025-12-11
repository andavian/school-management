package org.school.management.grades.domain.valueobject;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class PeriodGradeId {
    UUID value;

    private PeriodGradeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static PeriodGradeId generate() {
        return new PeriodGradeId(UUID.randomUUID());
    }

    public static PeriodGradeId from(String id) {
        try {
            return new PeriodGradeId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static PeriodGradeId from(UUID uuid) {
        return new PeriodGradeId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

