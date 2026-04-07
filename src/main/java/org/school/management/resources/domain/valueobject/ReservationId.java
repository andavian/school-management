package org.school.management.resources.domain.valueobject;

import java.util.UUID;

public record ReservationId(UUID value) {
    public ReservationId {
        if (value == null) throw new IllegalArgumentException("ReservationId cannot be null");
    }
    public static ReservationId of(UUID value) { return new ReservationId(value); }
    public static ReservationId generate() { return new ReservationId(UUID.randomUUID()); }
    public static ReservationId from(UUID uuid) { return of(uuid); }
    public static ReservationId from(String id) {
        try { return new ReservationId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ReservationId format: " + id);
        }
    }
    public String asString() { return value.toString(); }
}