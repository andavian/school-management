package org.school.management.academic.infra.web.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// ============================================================================
// ERROR RESPONSE DTO
// ============================================================================

record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}

record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors,
        String path
) {
    public static ValidationErrorResponse of(
            HttpStatus status,
            String message,
            Map<String, String> errors,
            String path) {
        return new ValidationErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                errors,
                path
        );
    }
}

// ============================================================================
// GLOBAL EXCEPTION HANDLER
// ============================================================================

@RestControllerAdvice(basePackages = "org.school.management.academic.infrastructure.web.controller")
@Slf4j
public class AcademicExceptionHandler {

    // ========================================================================
    // NOT FOUND EXCEPTIONS (404)
    // ========================================================================

    @ExceptionHandler({
            AcademicYearNotFoundException.class,
            OrientationNotFoundException.class,
            GradeLevelNotFoundException.class,
            SubjectNotFoundException.class,
            StudyPlanNotFoundException.class,
            EvaluationPeriodNotFoundException.class,
            QualificationRegistryNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            AcademicDomainException ex,
            WebRequest request) {

        log.warn("Not found exception: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ========================================================================
    // CONFLICT EXCEPTIONS (409)
    // ========================================================================

    @ExceptionHandler({
            AcademicYearAlreadyExistsException.class,
            AcademicYearAlreadyActiveException.class,
            OrientationAlreadyExistsException.class,
            GradeLevelAlreadyExistsException.class,
            SubjectAlreadyExistsException.class,
            StudyPlanAlreadyExistsException.class,
            SubjectAlreadyInPlanException.class,
            RegistryAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictException(
            AcademicDomainException ex,
            WebRequest request) {

        log.warn("Conflict exception: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ========================================================================
    // BAD REQUEST EXCEPTIONS (400)
    // ========================================================================

    @ExceptionHandler({
            InvalidAcademicYearDateException.class,
            InvalidOrientationForYearLevelException.class,
            OrientationNotActiveException.class,
            IncompatibleSubjectException.class,
            SubjectNotInPlanException.class,
            EvaluationPeriodOverlapException.class,
            EvaluationPeriodClosedException.class,
            NoActiveRegistryException.class,
            RegistryFullException.class,
            RegistryNotActiveException.class,
            InvalidFolioRangeException.class,
            GradeLevelFullException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            AcademicDomainException ex,
            WebRequest request) {

        log.warn("Bad request exception: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================================================
    // VALIDATION EXCEPTIONS (400)
    // ========================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse error = ValidationErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                validationErrors,
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================================================
    // ILLEGAL ARGUMENT EXCEPTION (400)
    // ========================================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================================================
    // GENERIC EXCEPTION (500)
    // ========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support.",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}