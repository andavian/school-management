// src/main/java/org/school/management/students/enrollment/domain/exception/StudentAlreadyEnrolledException.java
package org.school.management.students.enrollment.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.shared.person.domain.valueobject.DNI;


public class StudentAlreadyEnrolledException extends DomainException {
    public StudentAlreadyEnrolledException(DNI dni) {
        super("El alumno con DNI " + dni + " ya se encuentra matriculado en el establecimiento");
    }
}