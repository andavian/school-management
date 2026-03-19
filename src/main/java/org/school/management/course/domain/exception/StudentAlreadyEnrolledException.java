package org.school.management.course.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class StudentAlreadyEnrolledException extends DomainException {

    public StudentAlreadyEnrolledException(String message) {
        super(message);
    }

    public static StudentAlreadyEnrolledException inCourseSubject(
            UUID enrollmentId, UUID courseSubjectId) {
        return new StudentAlreadyEnrolledException(
                "Student enrollment: " + enrollmentId
                        + " is already enrolled in courseSubject: " + courseSubjectId);
    }
}