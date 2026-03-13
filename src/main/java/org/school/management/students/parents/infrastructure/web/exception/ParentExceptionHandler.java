package org.school.management.students.parents.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.students.parents.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class ParentExceptionHandler {

    // 404 — Padre no encontrado
    @ExceptionHandler(ParentNotFoundException.class)
    public ProblemDetail handleParentNotFound(ParentNotFoundException ex) {
        log.warn("Parent not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Parent Not Found");
        problem.setType(URI.create("/errors/parent-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Padre ya existe con ese DNI o email
    @ExceptionHandler(ParentAlreadyExistsException.class)
    public ProblemDetail handleParentAlreadyExists(ParentAlreadyExistsException ex) {
        log.warn("Parent already exists: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Parent Already Exists");
        problem.setType(URI.create("/errors/parent-already-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Estudiante ya tiene contacto principal
    @ExceptionHandler(DuplicatePrimaryContactException.class)
    public ProblemDetail handleDuplicatePrimaryContact(DuplicatePrimaryContactException ex) {
        log.warn("Duplicate primary contact: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Duplicate Primary Contact");
        problem.setType(URI.create("/errors/duplicate-primary-contact"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — Datos del padre inválidos
    @ExceptionHandler(InvalidParentDataException.class)
    public ProblemDetail handleInvalidParentData(InvalidParentDataException ex) {
        log.warn("Invalid parent data: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Parent Data");
        problem.setType(URI.create("/errors/invalid-parent-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — IllegalArgumentException del dominio
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument in parents: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Parent Data");
        problem.setType(URI.create("/errors/invalid-parent-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
