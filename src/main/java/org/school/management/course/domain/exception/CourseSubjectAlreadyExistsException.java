package org.school.management.course.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class CourseSubjectAlreadyExistsException extends DomainException {

    public CourseSubjectAlreadyExistsException(String message) {
        super(message);
    }

    public static CourseSubjectAlreadyExistsException forGradeLevelAndSubject(
            UUID gradeLevelId, UUID subjectId, int year) {
        return new CourseSubjectAlreadyExistsException(
                "CourseSubject already exists for gradeLevel: " + gradeLevelId
                        + ", subject: " + subjectId
                        + " in academic year: " + year);
    }
}