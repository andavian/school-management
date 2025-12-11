// src/main/java/org/school/management/students/enrollment/domain/repository/StudentEnrollmentRepository.java
package org.school.management.students.enrollment.domain.repository;

import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;

import java.util.Optional;

public interface StudentEnrollmentRepository {
    StudentEnrollment save(StudentEnrollment enrollment);

    Optional<StudentEnrollment> findById(EnrollmentId id);
    Optional<StudentEnrollment> findByUserId(UserId userId);
    Optional<StudentEnrollment> findByDni(DNI dni);

    boolean existsByUserId(UserId userId);
    boolean existsByDni(DNI dni);
}