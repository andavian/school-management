
package org.school.management.auth.domain.valueobject;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        if (value == null) throw new IllegalArgumentException("UserId cannot be null");
    }

    public static UserId of(UUID value)  { return new UserId(value); }
    public static UserId generate()       { return new UserId(UUID.randomUUID()); }
    public static UserId from(UUID uuid)  { return new UserId(uuid); }
    public static UserId from(String id) {
        try { return new UserId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }
    public String asString() { return value.toString(); }
}

