package org.school.management.teachers.domain.valueobject;

import java.util.UUID;

public record TeacherId(UUID value) {

    public TeacherId {
        if (value == null) throw new IllegalArgumentException("TeacherId cannot be null");
    }

    public static TeacherId of(UUID value)   { return new TeacherId(value); }
    public static TeacherId generate()        { return new TeacherId(UUID.randomUUID()); }
    public static TeacherId from(UUID uuid)   { return new TeacherId(uuid); }
    public static TeacherId from(String id) {
        try { return new TeacherId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TeacherId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}