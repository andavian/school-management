package org.school.management.students.enrollment.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.students.enrollment.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class EnrollmentExceptionHandler {

    // 404 — Inscripción no encontrada
    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ProblemDetail handleEnrollmentNotFound(EnrollmentNotFoundException ex) {
        log.warn("Enrollment not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Enrollment Not Found");
        problem.setType(URI.create("/errors/enrollment-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Inscripción ya completada
    @ExceptionHandler(EnrollmentAlreadyCompletedException.class)
    public ProblemDetail handleAlreadyCompleted(EnrollmentAlreadyCompletedException ex) {
        log.warn("Enrollment already completed: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Enrollment Already Completed");
        problem.setType(URI.create("/errors/enrollment-already-completed"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Inscripción ya dada de baja
    @ExceptionHandler(EnrollmentAlreadyWithdrawnException.class)
    public ProblemDetail handleAlreadyWithdrawn(EnrollmentAlreadyWithdrawnException ex) {
        log.warn("Enrollment already withdrawn: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Enrollment Already Withdrawn");
        problem.setType(URI.create("/errors/enrollment-already-withdrawn"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — Datos de cierre inválidos
    @ExceptionHandler(InvalidEnrollmentCompletionException.class)
    public ProblemDetail handleInvalidCompletion(InvalidEnrollmentCompletionException ex) {
        log.warn("Invalid enrollment completion: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Enrollment Completion");
        problem.setType(URI.create("/errors/invalid-enrollment-completion"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — Datos de inscripción inválidos
    @ExceptionHandler(InvalidEnrollmentException.class)
    public ProblemDetail handleInvalidEnrollment(InvalidEnrollmentException ex) {
        log.warn("Invalid enrollment: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Enrollment");
        problem.setType(URI.create("/errors/invalid-enrollment"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — IllegalArgumentException del dominio (VOs, formatos)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument in enrollment: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Enrollment Data");
        problem.setType(URI.create("/errors/invalid-enrollment-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}