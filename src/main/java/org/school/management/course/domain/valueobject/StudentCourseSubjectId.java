package org.school.management.course.domain.valueobject;

import java.util.UUID;

public record StudentCourseSubjectId(UUID value) {

    public StudentCourseSubjectId {
        if (value == null)
            throw new IllegalArgumentException("StudentCourseSubjectId cannot be null");
    }

    public static StudentCourseSubjectId of(UUID value)  { return new StudentCourseSubjectId(value); }
    public static StudentCourseSubjectId generate()       { return new StudentCourseSubjectId(UUID.randomUUID()); }
    public static StudentCourseSubjectId from(UUID uuid)  { return new StudentCourseSubjectId(uuid); }
    public static StudentCourseSubjectId from(String id) {
        try { return new StudentCourseSubjectId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid StudentCourseSubjectId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}