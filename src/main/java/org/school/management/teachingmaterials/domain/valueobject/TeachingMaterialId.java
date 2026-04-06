package org.school.management.teachingmaterials.domain.valueobject;

import java.util.UUID;

public record TeachingMaterialId(UUID value) {

    public TeachingMaterialId {
        if (value == null)
            throw new IllegalArgumentException("TeachingMaterialId cannot be null");
    }

    public static TeachingMaterialId of(UUID value)   { return new TeachingMaterialId(value); }
    public static TeachingMaterialId generate()        { return new TeachingMaterialId(UUID.randomUUID()); }
    public static TeachingMaterialId from(UUID uuid)   { return new TeachingMaterialId(uuid); }
    public static TeachingMaterialId from(String id) {
        try { return new TeachingMaterialId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TeachingMaterialId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}