package org.school.management.auth.infra.web.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.usecases.*;
import org.school.management.auth.application.usecases.admin.*;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotActiveException;
import org.school.management.auth.infra.web.controllers.AuthController;
import org.school.management.auth.infra.web.dto.response.ErrorApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ============================================
    // AUTHENTICATION EXCEPTIONS
    // ============================================

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorApiResponse> handleInvalidCredentials(
            InvalidPasswordException ex, WebRequest request) {

        log.warn("Invalid credentials: {}", ex.getMessage());

        var error = new ErrorApiResponse(
                false,
                "Credenciales inválidas",
                "INVALID_CREDENTIALS",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ErrorApiResponse> handleUserNotActive(
            UserNotActiveException ex, WebRequest request) {

        log.warn("User not active: {}", ex.getMessage());

        var error = new ErrorApiResponse(
                false,
                ex.getMessage(),
                "USER_NOT_ACTIVE",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AuthController.UnauthorizedException.class)
    public ResponseEntity<ErrorApiResponse> handleUnauthorized(
            AuthController.UnauthorizedException ex, WebRequest request) {

        var error = new ErrorApiResponse(
                false,
                ex.getMessage(),
                "UNAUTHORIZED",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ============================================
    // BUSINESS LOGIC EXCEPTIONS
    // ============================================

    @ExceptionHandler({
            CreateStudentUseCase.DniAlreadyExistsException.class,
            CreateTeacherUseCase.DniAlreadyExistsException.class
    })
    public ResponseEntity<ErrorApiResponse> handleDniAlreadyExists(
            RuntimeException ex, WebRequest request) {

        log.warn("DNI already exists: {}", ex.getMessage());

        var error = new ErrorApiResponse(
                false,
                ex.getMessage(),
                "DNI_ALREADY_EXISTS",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ChangePasswordUseCase.InvalidCurrentPasswordException.class)
    public ResponseEntity<ErrorApiResponse> handleInvalidCurrentPassword(
            ChangePasswordUseCase.InvalidCurrentPasswordException ex, WebRequest request) {

        var error = new ErrorApiResponse(
                false,
                ex.getMessage(),
                "INVALID_CURRENT_PASSWORD",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({
            ActivateTeacherAccountUseCase.InvalidTokenException.class
    })
    public ResponseEntity<ErrorApiResponse> handleInvalidToken(
            RuntimeException ex, WebRequest request) {

        var error = new ErrorApiResponse(
                false,
                ex.getMessage(),
                "INVALID_TOKEN",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ActivateTeacherAccountUseCase.UserNotFoundException.class)
    public ResponseEntity<ErrorApiResponse> handleUserNotFound(
            GetUserProfileUseCase.UserNotFoundException ex, WebRequest request) {

        var error = new ErrorApiResponse(
                false,
                ex.getMessage(),
                "USER_NOT_FOUND",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ============================================
    // VALIDATION EXCEPTIONS
    // ============================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApiResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<ErrorApiResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorApiResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        var error = new ErrorApiResponse(
                false,
                "Errores de validación en la solicitud",
                "VALIDATION_ERROR",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ============================================
    // AUTHORIZATION EXCEPTIONS
    // ============================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorApiResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        var error = new ErrorApiResponse(
                false,
                "No tiene permisos para acceder a este recurso",
                "ACCESS_DENIED",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ============================================
    // GENERIC EXCEPTION
    // ============================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApiResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: ", ex);

        var error = new ErrorApiResponse(
                false,
                "Error interno del servidor",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

