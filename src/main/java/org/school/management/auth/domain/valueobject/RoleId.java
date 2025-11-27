package org.school.management.auth.domain.valueobject;

import lombok.*;
import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class RoleId {
    UUID value;

    private RoleId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID());
    }

    public static RoleId from(String id) {
        try {
            return new RoleId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static RoleId from(UUID uuid) {
        return new RoleId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}
