// org.school.management.attendance.domain.valueobject.CourseAttendanceId
package org.school.management.attendance.domain.valueobject;

import java.util.UUID;

public record CourseAttendanceId(UUID value) {

    public CourseAttendanceId {
        if (value == null) throw new IllegalArgumentException("CourseAttendanceId cannot be null");
    }

    public static CourseAttendanceId of(UUID value)    { return new CourseAttendanceId(value); }
    public static CourseAttendanceId from(UUID uuid)   { return new CourseAttendanceId(uuid); }
    public static CourseAttendanceId generate()        { return new CourseAttendanceId(UUID.randomUUID()); }
    public static CourseAttendanceId from(String id) {
        try { return new CourseAttendanceId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CourseAttendanceId format: " + id);
        }
    }

    public String asString() { return value.toString(); }
}