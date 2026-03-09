package org.school.management.students.personal.domain.valueobject;

import java.util.UUID;

public record StudentPersonalDataId(UUID value) {

    public StudentPersonalDataId {
        if (value == null) {
            throw new IllegalArgumentException("StudentPersonalDataId cannot be null");
        }
    }

    public static StudentPersonalDataId generate() {
        return new StudentPersonalDataId(UUID.randomUUID());
    }

    public static StudentPersonalDataId from(UUID uuid) {
        return new StudentPersonalDataId(uuid);
    }

    public static StudentPersonalDataId from(String id) {
        try {
            return new StudentPersonalDataId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid StudentPersonalDataId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}