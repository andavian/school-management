package org.school.management.students.personal.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.students.personal.domain.exception.InvalidStudentDataException;
import org.school.management.students.personal.domain.exception.StudentAlreadyExistsException;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Manejador de excepciones del bounded context students/personal.
 *
 * Usa ProblemDetail (RFC 9457 / Spring 6) — consistente con el resto del proyecto.
 * Solo maneja excepciones de dominio de este bounded context.
 * Las excepciones genéricas (validación Jakarta, autenticación, etc.)
 * las maneja el GlobalExceptionHandler del proyecto.
 */
@RestControllerAdvice
@Slf4j
public class StudentExceptionHandler {

    // ── 404 Not Found ─────────────────────────────────────────────────────

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFound(StudentNotFoundException ex) {
        log.warn("Student not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Student Not Found");
        problem.setType(URI.create("/errors/student-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // ── 409 Conflict ──────────────────────────────────────────────────────

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ProblemDetail handleStudentAlreadyExists(StudentAlreadyExistsException ex) {
        log.warn("Student already exists: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setTitle("Student Already Exists");
        problem.setType(URI.create("/errors/student-already-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // ── 422 Unprocessable Entity ──────────────────────────────────────────

    @ExceptionHandler(InvalidStudentDataException.class)
    public ProblemDetail handleInvalidStudentData(InvalidStudentDataException ex) {
        log.warn("Invalid student data: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
        problem.setTitle("Invalid Student Data");
        problem.setType(URI.create("/errors/invalid-student-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // ── 422 — IllegalArgumentException del dominio ────────────────────────
    // Cubre casos como CUIL↔DNI mismatch que lanza IllegalArgumentException
    // directamente desde StudentPersonalData.create()

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Domain validation failed: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
        problem.setTitle("Validation Error");
        problem.setType(URI.create("/errors/validation-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // ── 500 — IllegalStateException (sin año académico activo, etc.) ──────

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state in student operation: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
        problem.setTitle("Operation Not Possible");
        problem.setType(URI.create("/errors/operation-not-possible"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}