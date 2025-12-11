package org.school.management.academic.domain.valueobject.ids;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class PeriodId {
    UUID value;

    public PeriodId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static PeriodId generate() {
        return new PeriodId(UUID.randomUUID());
    }

    public static PeriodId from(String id) {
        try {
            return new PeriodId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static PeriodId from(UUID uuid) {
        return new PeriodId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

