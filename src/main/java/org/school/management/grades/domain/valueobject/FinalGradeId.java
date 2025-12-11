package org.school.management.grades.domain.valueobject;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class FinalGradeId {
    UUID value;

    private FinalGradeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static FinalGradeId generate() {
        return new FinalGradeId(UUID.randomUUID());
    }

    public static FinalGradeId from(String id) {
        try {
            return new FinalGradeId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static FinalGradeId from(UUID uuid) {
        return new FinalGradeId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

