package org.school.management.resources.infrastructure.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.domain.exception.InsufficientResourceUnitsException;
import org.school.management.resources.domain.exception.InvalidReservationStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Manejador global de excepciones para el BC resources/.
 * Scoped exclusivamente a los controllers de este módulo para evitar colisiones.
 */
@RestControllerAdvice(basePackages = "org.school.management.resources.infrastructure.web.controller")
@Slf4j
public class ResourcesExceptionHandler {

    @ExceptionHandler(InsufficientResourceUnitsException.class)
    public ProblemDetail handleInsufficientUnits(InsufficientResourceUnitsException ex, HttpServletRequest request) {
        log.warn("Intento de reserva con stock insuficiente: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Recursos insuficientes", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidReservationStateException.class)
    public ProblemDetail handleInvalidState(InvalidReservationStateException ex, HttpServletRequest request) {
        log.warn("Transición de estado inválida en reserva: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Transición de estado inválida", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Datos de entrada inválidos en resources/: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Datos de entrada inválidos", ex.getMessage(), request);
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("/errors/" + title.toLowerCase().replace(" ", "-")));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}