package org.school.management.students.records.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class RecordExceptionHandler {

    // 404 — Legajo no encontrado
    @ExceptionHandler(RecordNotFoundException.class)
    public ProblemDetail handleRecordNotFound(RecordNotFoundException ex) {
        log.warn("Record not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Record Not Found");
        problem.setType(URI.create("/errors/record-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 404 — Documento no encontrado
    @ExceptionHandler(DocumentNotFoundException.class)
    public ProblemDetail handleDocumentNotFound(DocumentNotFoundException ex) {
        log.warn("Document not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Document Not Found");
        problem.setType(URI.create("/errors/document-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Legajo ya aprobado
    @ExceptionHandler(RecordAlreadyApprovedException.class)
    public ProblemDetail handleRecordAlreadyApproved(RecordAlreadyApprovedException ex) {
        log.warn("Record already approved: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Record Already Approved");
        problem.setType(URI.create("/errors/record-already-approved"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 409 — Documento ya aprobado
    @ExceptionHandler(DocumentAlreadyApprovedException.class)
    public ProblemDetail handleDocumentAlreadyApproved(DocumentAlreadyApprovedException ex) {
        log.warn("Document already approved: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Document Already Approved");
        problem.setType(URI.create("/errors/document-already-approved"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — Legajo incompleto para revisión
    @ExceptionHandler(IncompleteRecordException.class)
    public ProblemDetail handleIncompleteRecord(IncompleteRecordException ex) {
        log.warn("Incomplete record: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Incomplete Record");
        problem.setType(URI.create("/errors/incomplete-record"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — Legajo no listo para aprobación
    @ExceptionHandler(RecordNotReadyForApprovalException.class)
    public ProblemDetail handleNotReadyForApproval(RecordNotReadyForApprovalException ex) {
        log.warn("Record not ready for approval: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Record Not Ready For Approval");
        problem.setType(URI.create("/errors/record-not-ready-for-approval"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 422 — IllegalArgumentException del dominio
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument in record: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Record Data");
        problem.setType(URI.create("/errors/invalid-record-data"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
