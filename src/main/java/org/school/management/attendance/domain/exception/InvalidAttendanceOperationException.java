package org.school.management.attendance.domain.exception;

// En: org.school.management.attendance.domain.exception
public class InvalidAttendanceOperationException extends RuntimeException {
    public InvalidAttendanceOperationException(String message) {
        super(message);
    }
}