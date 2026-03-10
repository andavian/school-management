
package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record AcademicYearId(UUID value) {

    public AcademicYearId {
        if (value == null) throw new IllegalArgumentException("AcademicYearId cannot be null");
    }

    public static AcademicYearId of(UUID value)   { return new AcademicYearId(value); }
    public static AcademicYearId generate()        { return new AcademicYearId(UUID.randomUUID()); }
    public static AcademicYearId from(UUID uuid)   { return new AcademicYearId(uuid); }
    public static AcademicYearId from(String id) {
        try { return new AcademicYearId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AcademicYearId format: " + id);
        }
    }
    public String asString() { return value.toString(); }
}

