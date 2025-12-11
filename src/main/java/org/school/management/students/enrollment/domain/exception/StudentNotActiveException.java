
package org.school.management.students.enrollment.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;

public class StudentNotActiveException extends DomainException {
    public StudentNotActiveException(EnrollmentId id) {
        super("El alumno con matrícula " + id + " no está activo");
    }
}