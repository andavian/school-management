package org.school.management.auth.domain.valueobject;

import lombok.Value;
import java.util.Objects;
import java.util.UUID;

@Value
public class BlacklistedTokenId {
    UUID value;

    private BlacklistedTokenId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("BlacklistedTokenId cannot be null");
        }
        this.value = value;
    }

    public static BlacklistedTokenId generate() {
        return new BlacklistedTokenId(UUID.randomUUID());
    }

    public static BlacklistedTokenId from(UUID uuid) {
        return new BlacklistedTokenId(uuid);
    }

    public static BlacklistedTokenId from(String id) {
        try {
            return new BlacklistedTokenId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid BlacklistedTokenId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}