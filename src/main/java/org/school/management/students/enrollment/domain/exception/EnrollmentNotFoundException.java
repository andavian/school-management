package org.school.management.students.enrollment.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class EnrollmentNotFoundException extends DomainException {

    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public static EnrollmentNotFoundException byId(UUID enrollmentId) {
        return new EnrollmentNotFoundException(
                "Enrollment not found with id: " + enrollmentId
        );
    }

    public static EnrollmentNotFoundException byStudentAndYear(UUID studentId, UUID academicYearId) {
        return new EnrollmentNotFoundException(
                "No enrollment found for studentId: " + studentId
                        + " in academicYearId: " + academicYearId
        );
    }
}