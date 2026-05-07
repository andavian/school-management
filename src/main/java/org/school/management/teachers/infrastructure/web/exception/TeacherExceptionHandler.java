package org.school.management.teachers.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.teachers.domain.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TeacherExceptionHandler {

    @ExceptionHandler(TeacherNotFoundException.class)
    public ProblemDetail handleTeacherNotFound(TeacherNotFoundException ex) {
        log.warn("Teacher not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Teacher Not Found");
        problem.setType(URI.create("/errors/teacher-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(TeacherAlreadyExistsException.class)
    public ProblemDetail handleTeacherAlreadyExists(TeacherAlreadyExistsException ex) {
        log.warn("Teacher already exists: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Teacher Already Exists");
        problem.setType(URI.create("/errors/teacher-already-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidTeacherDataException.class)
    public ProblemDetail handleInvalidTeacherData(InvalidTeacherDataException ex) {
        log.warn("Invalid teacher data: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Teacher Data");
        problem.setType(URI.create("/errors/invalid-teacher-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state in teachers: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Invalid Teacher State");
        problem.setType(URI.create("/errors/invalid-teacher-state"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
