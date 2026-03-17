package org.school.management.grades.domain.valueobject;

import java.util.UUID;

public record PeriodGradeId(UUID value) {

    public PeriodGradeId {
        if (value == null) throw new IllegalArgumentException("PeriodGradeId cannot be null");
    }

    public static PeriodGradeId of(UUID value)  { return new PeriodGradeId(value); }
    public static PeriodGradeId generate()       { return new PeriodGradeId(UUID.randomUUID()); }
    public static PeriodGradeId from(UUID uuid)  { return new PeriodGradeId(uuid); }
    public static PeriodGradeId from(String id) {
        try { return new PeriodGradeId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PeriodGradeId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}