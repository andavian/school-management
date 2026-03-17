package org.school.management.grades.domain.valueobject;

import java.util.UUID;

public record FinalGradeId(UUID value) {

    public FinalGradeId {
        if (value == null) throw new IllegalArgumentException("FinalGradeId cannot be null");
    }

    public static FinalGradeId of(UUID value)  { return new FinalGradeId(value); }
    public static FinalGradeId generate()       { return new FinalGradeId(UUID.randomUUID()); }
    public static FinalGradeId from(UUID uuid)  { return new FinalGradeId(uuid); }
    public static FinalGradeId from(String id) {
        try { return new FinalGradeId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FinalGradeId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}