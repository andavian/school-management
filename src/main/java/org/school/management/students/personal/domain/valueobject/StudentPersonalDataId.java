package org.school.management.students.personal.domain.valueobject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class StudentPersonalDataId {
    UUID value;

    private StudentPersonalDataId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static StudentPersonalDataId generate() {
        return new StudentPersonalDataId(UUID.randomUUID());
    }

    public static StudentPersonalDataId from(String id) {
        try {
            return new StudentPersonalDataId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static StudentPersonalDataId from(UUID uuid) {
        return new StudentPersonalDataId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

