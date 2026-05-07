package org.school.management.geography.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.domain.exception.CountryNotFoundException;
import org.school.management.geography.domain.exception.DuplicatePlaceException;
import org.school.management.geography.domain.exception.PlaceNotFoundException;
import org.school.management.geography.domain.exception.ProvinceNotFoundException;
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
public class GeographyExceptionHandler {

    @ExceptionHandler(CountryNotFoundException.class)
    public ProblemDetail handleCountryNotFound(CountryNotFoundException ex) {
        log.warn("Country not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Country Not Found");
        problem.setType(URI.create("/errors/country-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(ProvinceNotFoundException.class)
    public ProblemDetail handleProvinceNotFound(ProvinceNotFoundException ex) {
        log.warn("Province not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Province Not Found");
        problem.setType(URI.create("/errors/province-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(PlaceNotFoundException.class)
    public ProblemDetail handlePlaceNotFound(PlaceNotFoundException ex) {
        log.warn("Place not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Place Not Found");
        problem.setType(URI.create("/errors/place-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(DuplicatePlaceException.class)
    public ProblemDetail handleDuplicatePlace(DuplicatePlaceException ex) {
        log.warn("Duplicate place: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Duplicate Place");
        problem.setType(URI.create("/errors/duplicate-place"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
