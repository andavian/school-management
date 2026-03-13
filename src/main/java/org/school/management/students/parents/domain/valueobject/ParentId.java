package org.school.management.students.parents.domain.valueobject;

import java.util.UUID;

public record ParentId(UUID value) {

    public ParentId {
        if (value == null) throw new IllegalArgumentException("ParentId cannot be null");
    }

    public static ParentId of(UUID value)    { return new ParentId(value); }
    public static ParentId from(UUID value)  { return new ParentId(value); }
    public static ParentId from(String id) {
        try { return new ParentId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ParentId format: " + id);
        }
    }
    public static ParentId generate()        { return new ParentId(UUID.randomUUID()); }

    public String asString() { return value.toString(); }

    @Override
    public String toString() { return value.toString(); }
}