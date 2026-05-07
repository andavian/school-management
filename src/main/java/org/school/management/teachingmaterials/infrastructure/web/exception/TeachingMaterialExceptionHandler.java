package org.school.management.teachingmaterials.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialAccessDeniedException;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialNotFoundException;
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
public class TeachingMaterialExceptionHandler {

    @ExceptionHandler(TeachingMaterialNotFoundException.class)
    public ProblemDetail handleNotFound(TeachingMaterialNotFoundException ex) {
        log.warn("TeachingMaterial not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Teaching Material Not Found");
        problem.setType(URI.create("/errors/teaching-material-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(TeachingMaterialAccessDeniedException.class)
    public ProblemDetail handleAccessDenied(TeachingMaterialAccessDeniedException ex) {
        log.warn("TeachingMaterial access denied: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Teaching Material Access Denied");
        problem.setType(URI.create("/errors/teaching-material-access-denied"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid teaching material data: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Teaching Material Data");
        problem.setType(URI.create("/errors/invalid-teaching-material-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.error("Teaching material storage error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Storage Error");
        problem.setType(URI.create("/errors/teaching-material-storage-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}