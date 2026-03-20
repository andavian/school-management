// org.school.management.attendance.domain.exception.AttendanceNotFoundException
package org.school.management.attendance.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class AttendanceNotFoundException extends DomainException {

    public AttendanceNotFoundException(String message) {
        super(message);
    }

    public static AttendanceNotFoundException dailyById(UUID id) {
        return new AttendanceNotFoundException("Daily attendance not found with id: " + id);
    }

    public static AttendanceNotFoundException courseById(UUID id) {
        return new AttendanceNotFoundException("Course attendance not found with id: " + id);
    }

    public static AttendanceNotFoundException summaryByStudentAndPeriod(UUID studentCourseSubjectId,
                                                                        UUID periodId) {
        return new AttendanceNotFoundException(
                "Attendance summary not found for studentCourseSubjectId=" + studentCourseSubjectId +
                        " and periodId=" + periodId);
    }
}