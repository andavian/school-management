package org.school.management.students.enrollment.domain.valueobject;

import java.util.UUID;

public record EnrollmentId(UUID value) {

    public EnrollmentId {
        if (value == null) throw new IllegalArgumentException("EnrollmentId cannot be null");
    }

    public static EnrollmentId of(UUID value)   { return new EnrollmentId(value); }
    public static EnrollmentId from(UUID value) { return new EnrollmentId(value); }
    public static EnrollmentId from(String id) {
        try { return new EnrollmentId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EnrollmentId format: " + id);
        }
    }
    public static EnrollmentId generate()       { return new EnrollmentId(UUID.randomUUID()); }

    public String asString() { return value.toString(); }

    @Override
    public String toString() { return value.toString(); }
}