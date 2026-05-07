// src/main/java/org/school/management/resources/infrastructure/web/exception/ResourcesExceptionHandler.java
package org.school.management.resources.infrastructure.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Manejador de excepciones específico para el bounded context resources.
 */
@RestControllerAdvice(basePackages = "org.school.management.resources.infrastructure.web.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ResourcesExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return buildProblem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ProblemDetail handleReservationNotFound(ReservationNotFoundException ex, HttpServletRequest request) {
        log.warn("Reserva no encontrada: {}", ex.getMessage());
        return buildProblem(HttpStatus.NOT_FOUND, "Reservation Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientResourceUnitsException.class)
    public ProblemDetail handleInsufficientUnits(InsufficientResourceUnitsException ex, HttpServletRequest request) {
        log.warn("Stock insuficiente: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient Resource Units", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidReservationStateException.class)
    public ProblemDetail handleInvalidState(InvalidReservationStateException ex, HttpServletRequest request) {
        log.warn("Transición de estado inválida: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Reservation State", ex.getMessage(), request);
    }

    @ExceptionHandler(ReservationAccessDeniedException.class)
    public ProblemDetail handleAccessDenied(ReservationAccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acceso denegado a reserva: {}", ex.getMessage());
        return buildProblem(HttpStatus.FORBIDDEN, "Reservation Access Denied", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Datos inválidos en resources: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Input", ex.getMessage(), request);
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("/errors/" + title.toLowerCase().replace(" ", "-").replace(" ", "")));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}