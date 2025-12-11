package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class RegistryId {
    UUID value;

    public RegistryId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static RegistryId generate() {
        return new RegistryId(UUID.randomUUID());
    }

    public static RegistryId from(String id) {
        try {
            return new RegistryId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static RegistryId from(UUID uuid) {
        return new RegistryId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}

