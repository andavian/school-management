package org.school.management.students.health.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.students.health.domain.exception.HealthRecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Manejador de excepciones del módulo students/health.
 * Usa ProblemDetail (RFC 9457) — consistente con el resto del proyecto.
 */
@RestControllerAdvice
@Slf4j
public class HealthRecordExceptionHandler {

    @ExceptionHandler(HealthRecordNotFoundException.class)
    public ProblemDetail handleHealthRecordNotFound(HealthRecordNotFoundException ex) {
        log.warn("Health record not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Health Record Not Found");
        problem.setType(URI.create("/errors/health-record-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid health record data: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Health Record Data");
        problem.setType(URI.create("/errors/invalid-health-record-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}