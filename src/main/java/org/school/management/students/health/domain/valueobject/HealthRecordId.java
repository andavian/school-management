package org.school.management.students.health.domain.valueobject;

import java.util.UUID;

public record HealthRecordId(UUID value) {

    public HealthRecordId {
        if (value == null)
            throw new IllegalArgumentException("HealthRecordId cannot be null");
    }

    public static HealthRecordId of(UUID value)   { return new HealthRecordId(value); }
    public static HealthRecordId generate()        { return new HealthRecordId(UUID.randomUUID()); }
    public static HealthRecordId from(UUID uuid)   { return new HealthRecordId(uuid); }
    public static HealthRecordId from(String id) {
        try { return new HealthRecordId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid HealthRecordId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}