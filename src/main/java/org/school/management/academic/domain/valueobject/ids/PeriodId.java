package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record PeriodId(UUID value) {

    public PeriodId {
        if (value == null) throw new IllegalArgumentException("PeriodId cannot be null");
    }

    public static PeriodId of(UUID value) {
        return new PeriodId(value);
    }

    public static PeriodId generate() {
        return new PeriodId(UUID.randomUUID());
    }

    public static PeriodId from(UUID uuid) {
        return new PeriodId(uuid);
    }

    public static PeriodId from(String id) {
        try {
            return new PeriodId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PeriodId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
