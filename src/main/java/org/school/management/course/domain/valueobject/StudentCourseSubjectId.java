package org.school.management.course.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class StudentCourseSubjectId {
    UUID value;

    private StudentCourseSubjectId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static StudentCourseSubjectId generate() {
        return new StudentCourseSubjectId(UUID.randomUUID());
    }

    public static StudentCourseSubjectId from(String id) {
        try {
            return new StudentCourseSubjectId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static StudentCourseSubjectId from(UUID uuid) {
        return new StudentCourseSubjectId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

