package org.school.management.course.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class StudentCourseSubjectNotFoundException extends DomainException {

    public StudentCourseSubjectNotFoundException(String message) {
        super(message);
    }

    public static StudentCourseSubjectNotFoundException byId(UUID id) {
        return new StudentCourseSubjectNotFoundException(
                "StudentCourseSubject not found with id: " + id);
    }

    public static StudentCourseSubjectNotFoundException byEnrollmentAndCourse(
            UUID enrollmentId, UUID courseSubjectId) {
        return new StudentCourseSubjectNotFoundException(
                "StudentCourseSubject not found for enrollment: " + enrollmentId
                        + " and courseSubject: " + courseSubjectId);
    }
}