package org.school.management.auth.domain.valueobject;

import java.util.UUID;

public record ConfirmationTokenId(UUID value) {

    public ConfirmationTokenId {
        if (value == null) {
            throw new IllegalArgumentException("ConfirmationTokenId cannot be null");
        }
    }

    public static ConfirmationTokenId of(UUID value) {
        return new ConfirmationTokenId(value);
    }

    public static ConfirmationTokenId generate() {
        return new ConfirmationTokenId(UUID.randomUUID());
    }

    public static ConfirmationTokenId from(UUID uuid) {
        return new ConfirmationTokenId(uuid);
    }

    public static ConfirmationTokenId from(String id) {
        try {
            return new ConfirmationTokenId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ConfirmationTokenId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}