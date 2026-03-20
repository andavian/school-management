// org.school.management.attendance.domain.valueobject.AttendanceSummaryId
package org.school.management.attendance.domain.valueobject;

import java.util.UUID;

public record AttendanceSummaryId(UUID value) {

    public AttendanceSummaryId {
        if (value == null) throw new IllegalArgumentException("AttendanceSummaryId cannot be null");
    }

    public static AttendanceSummaryId of(UUID value)    { return new AttendanceSummaryId(value); }
    public static AttendanceSummaryId from(UUID uuid)   { return new AttendanceSummaryId(uuid); }
    public static AttendanceSummaryId generate()        { return new AttendanceSummaryId(UUID.randomUUID()); }
    public static AttendanceSummaryId from(String id) {
        try { return new AttendanceSummaryId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AttendanceSummaryId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}