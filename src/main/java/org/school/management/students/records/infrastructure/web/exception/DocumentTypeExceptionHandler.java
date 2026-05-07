package org.school.management.students.records.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.domain.exception.DocumentTypeAlreadyExistsException;
import org.school.management.students.records.domain.exception.DocumentTypeNotFoundException;
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
public class DocumentTypeExceptionHandler {

    @ExceptionHandler(DocumentTypeNotFoundException.class)
    public ProblemDetail handleNotFound(DocumentTypeNotFoundException ex) {
        log.warn("DocumentType not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Document Type Not Found");
        problem.setType(URI.create("/errors/document-type-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(DocumentTypeAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(DocumentTypeAlreadyExistsException ex) {
        log.warn("DocumentType already exists: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Document Type Already Exists");
        problem.setType(URI.create("/errors/document-type-already-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}