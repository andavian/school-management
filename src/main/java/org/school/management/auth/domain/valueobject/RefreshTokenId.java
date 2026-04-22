package org.school.management.auth.domain.valueobject;

import java.util.UUID;

public record RefreshTokenId(UUID value) {

    public RefreshTokenId {
        if (value == null) {
            throw new IllegalArgumentException("RefreshTokenId cannot be null");
        }
    }

    public static RefreshTokenId generate() {
        return new RefreshTokenId(UUID.randomUUID());
    }

    public static RefreshTokenId of(UUID value) {
        return new RefreshTokenId(value);
    }

    public static RefreshTokenId from(UUID uuid) {
        return new RefreshTokenId(uuid);
    }

    public static RefreshTokenId from(String id) {
        try {
            return new RefreshTokenId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ConfirmationTokenId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
