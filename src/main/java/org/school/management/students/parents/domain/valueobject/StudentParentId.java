package org.school.management.students.parents.domain.valueobject;

import java.util.UUID;

public record StudentParentId(UUID value) {

    public StudentParentId {
        if (value == null) throw new IllegalArgumentException("StudentParentId cannot be null");
    }

    public static StudentParentId of(UUID value)    { return new StudentParentId(value); }
    public static StudentParentId from(UUID value)  { return new StudentParentId(value); }
    public static StudentParentId from(String id) {
        try { return new StudentParentId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid StudentParentId format: " + id);
        }
    }
    public static StudentParentId generate()        { return new StudentParentId(UUID.randomUUID()); }

    public String asString() { return value.toString(); }

    @Override
    public String toString() { return value.toString(); }
}