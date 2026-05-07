package org.school.management.grades.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.grades.domain.exception.GradeAlreadyRecordedInRegistryException;
import org.school.management.grades.domain.exception.GradeAlreadyValidatedException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GradesExceptionHandler {

    @ExceptionHandler(GradeNotFoundException.class)
    public ProblemDetail handleGradeNotFound(GradeNotFoundException ex) {
        log.warn("Grade not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Grade Not Found");
        problem.setType(URI.create("/errors/grade-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(GradeAlreadyValidatedException.class)
    public ProblemDetail handleGradeAlreadyValidated(GradeAlreadyValidatedException ex) {
        log.warn("Grade already validated: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Grade Already Validated");
        problem.setType(URI.create("/errors/grade-already-validated"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(GradeAlreadyRecordedInRegistryException.class)
    public ProblemDetail handleGradeAlreadyRecorded(
            GradeAlreadyRecordedInRegistryException ex) {
        log.warn("Grade already recorded in registry: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Grade Already Recorded In Registry");
        problem.setType(URI.create("/errors/grade-already-recorded"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidGradeException.class)
    public ProblemDetail handleInvalidGrade(InvalidGradeException ex) {
        log.warn("Invalid grade operation: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Grade Operation");
        problem.setType(URI.create("/errors/invalid-grade"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
