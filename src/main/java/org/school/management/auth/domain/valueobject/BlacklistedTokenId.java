package org.school.management.auth.domain.valueobject;

import java.util.UUID;

public record BlacklistedTokenId(UUID value) {

    public BlacklistedTokenId {
        if (value == null) throw new IllegalArgumentException("BlacklistedTokenId cannot be null");
    }

    public static BlacklistedTokenId of(UUID value) {
        return new BlacklistedTokenId(value);
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
