package org.school.management.course.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class CourseSubjectNotFoundException extends DomainException {

    public CourseSubjectNotFoundException(String message) {
        super(message);
    }

    public static CourseSubjectNotFoundException byId(UUID id) {
        return new CourseSubjectNotFoundException(
                "CourseSubject not found with id: " + id);
    }

    public static CourseSubjectNotFoundException byGradeLevelAndSubject(
            UUID gradeLevelId, UUID subjectId) {
        return new CourseSubjectNotFoundException(
                "CourseSubject not found for gradeLevel: " + gradeLevelId
                        + " and subject: " + subjectId);
    }
}