package org.school.management.course.infrastructure.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.school.management.course.domain.exception.CourseSubjectAlreadyExistsException;
import org.school.management.course.domain.exception.CourseSubjectNotFoundException;
import org.school.management.course.domain.exception.StudentAlreadyEnrolledException;
import org.school.management.course.domain.exception.StudentCourseSubjectNotFoundException;
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
public class CourseExceptionHandler {

    @ExceptionHandler(CourseSubjectNotFoundException.class)
    public ProblemDetail handleCourseSubjectNotFound(CourseSubjectNotFoundException ex) {
        log.warn("CourseSubject not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Course Subject Not Found");
        problem.setType(URI.create("/errors/course-subject-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(StudentCourseSubjectNotFoundException.class)
    public ProblemDetail handleStudentCourseSubjectNotFound(StudentCourseSubjectNotFoundException ex) {
        log.warn("StudentCourseSubject not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Student Course Subject Not Found");
        problem.setType(URI.create("/errors/student-course-subject-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(CourseSubjectAlreadyExistsException.class)
    public ProblemDetail handleCourseSubjectAlreadyExists(CourseSubjectAlreadyExistsException ex) {
        log.warn("CourseSubject already exists: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Course Subject Already Exists");
        problem.setType(URI.create("/errors/course-subject-already-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(StudentAlreadyEnrolledException.class)
    public ProblemDetail handleStudentAlreadyEnrolled(StudentAlreadyEnrolledException ex) {
        log.warn("Student already enrolled: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Student Already Enrolled");
        problem.setType(URI.create("/errors/student-already-enrolled"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state in course: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Invalid Course State");
        problem.setType(URI.create("/errors/invalid-course-state"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
