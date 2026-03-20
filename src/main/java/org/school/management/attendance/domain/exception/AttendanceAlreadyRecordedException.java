// org.school.management.attendance.domain.exception.AttendanceAlreadyRecordedException
package org.school.management.attendance.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.time.LocalDate;
import java.util.UUID;

public class AttendanceAlreadyRecordedException extends DomainException {

    public AttendanceAlreadyRecordedException(String message) {
        super(message);
    }

    public static AttendanceAlreadyRecordedException forDailyAttendance(UUID studentId, LocalDate date) {
        return new AttendanceAlreadyRecordedException(
                "Daily attendance already recorded for student " + studentId + " on " + date);
    }

    public static AttendanceAlreadyRecordedException forCourseAttendance(UUID studentCourseSubjectId,
                                                                         LocalDate classDate) {
        return new AttendanceAlreadyRecordedException(
                "Course attendance already recorded for studentCourseSubject " +
                        studentCourseSubjectId + " on " + classDate);
    }
}