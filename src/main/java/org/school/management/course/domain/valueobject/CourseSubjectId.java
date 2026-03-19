package org.school.management.course.domain.valueobject;

import java.util.UUID;

public record CourseSubjectId(UUID value) {

    public CourseSubjectId {
        if (value == null)
            throw new IllegalArgumentException("CourseSubjectId cannot be null");
    }

    public static CourseSubjectId of(UUID value)  { return new CourseSubjectId(value); }
    public static CourseSubjectId generate()       { return new CourseSubjectId(UUID.randomUUID()); }
    public static CourseSubjectId from(UUID uuid)  { return new CourseSubjectId(uuid); }
    public static CourseSubjectId from(String id) {
        try { return new CourseSubjectId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CourseSubjectId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}