// org.school.management.attendance.domain.valueobject.DailyAttendanceId
package org.school.management.attendance.domain.valueobject;

import java.util.UUID;

public record DailyAttendanceId(UUID value) {

    public DailyAttendanceId {
        if (value == null) throw new IllegalArgumentException("DailyAttendanceId cannot be null");
    }

    public static DailyAttendanceId of(UUID value)    { return new DailyAttendanceId(value); }
    public static DailyAttendanceId from(UUID uuid)   { return new DailyAttendanceId(uuid); }
    public static DailyAttendanceId generate()        { return new DailyAttendanceId(UUID.randomUUID()); }
    public static DailyAttendanceId from(String id) {
        try { return new DailyAttendanceId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DailyAttendanceId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}