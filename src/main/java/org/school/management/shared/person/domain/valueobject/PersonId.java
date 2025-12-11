package org.school.management.shared.person.domain.valueobject;

import java.util.UUID;

public record PersonId (UUID value){


    public PersonId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static PersonId generate() {
        return new PersonId(UUID.randomUUID());
    }

    public static PersonId from(String id) {
        try {
            return new PersonId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static PersonId from(UUID uuid) {
        return new PersonId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

