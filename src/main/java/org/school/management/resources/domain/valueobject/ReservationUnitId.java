package org.school.management.resources.domain.valueobject;

import java.util.UUID;

public record ReservationUnitId(UUID value) {
    public ReservationUnitId {
        if (value == null) throw new IllegalArgumentException("ReservationUnitId cannot be null");
    }

    public static ReservationUnitId of(UUID value) { return new ReservationUnitId(value); }
    public static ReservationUnitId generate() { return new ReservationUnitId(UUID.randomUUID()); }
    public static ReservationUnitId from(UUID uuid) { return new ReservationUnitId(uuid); }
    public static ReservationUnitId from(String id) {
        try { return new ReservationUnitId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ReservationUnitId format: " + id);
        }
    }
}