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

@RestControllerAdvice(basePackages = "org.school.management.resources.infrastructure.web")
@Slf4j
public class ResourcesExceptionHandler {

    @ExceptionHandler(InsufficientResourceUnitsException.class)
    public ProblemDetail handleInsufficientUnits(InsufficientResourceUnitsException ex, HttpServletRequest request) {
        log.warn("Intento de reserva con stock insuficiente: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Recursos insuficientes");
        problem.setType(URI.create("/errors/insufficient-resource-units"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(InvalidReservationStateException.class)
    public ProblemDetail handleInvalidState(InvalidReservationStateException ex, HttpServletRequest request) {
        log.warn("Transición de estado inválida: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Transición de estado inválida");
        problem.setType(URI.create("/errors/invalid-reservation-state"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Datos de entrada inválidos");
        problem.setType(URI.create("/errors/validation-error"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}