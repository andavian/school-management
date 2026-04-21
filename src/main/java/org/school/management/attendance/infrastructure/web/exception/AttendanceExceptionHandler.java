// org.school.management.attendance.infrastructure.web.exception.AttendanceExceptionHandler
package org.school.management.attendance.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.exception.AttendanceAlreadyRecordedException;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.exception.InvalidAttendanceOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class AttendanceExceptionHandler {

    @ExceptionHandler(AttendanceNotFoundException.class)
    public ProblemDetail handleAttendanceNotFound(AttendanceNotFoundException ex) {
        log.warn("AttendanceNotFoundException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Attendance Not Found");
        problem.setType(URI.create("/errors/attendance-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AttendanceAlreadyRecordedException.class)
    public ProblemDetail handleAttendanceAlreadyRecorded(AttendanceAlreadyRecordedException ex) {
        log.warn("AttendanceAlreadyRecordedException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Attendance Already Recorded");
        problem.setType(URI.create("/errors/attendance-already-recorded"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidAttendanceOperationException.class) // <-- Ahora es específica
    public ProblemDetail handleInvalidAttendanceOperation(InvalidAttendanceOperationException ex) {
        log.warn("Invalid attendance operation: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Attendance Operation");
        problem.setType(URI.create("/errors/invalid-attendance-operation"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

}