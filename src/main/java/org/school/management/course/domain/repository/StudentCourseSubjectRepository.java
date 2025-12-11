package org.school.management.course.domain.repository;

import org.school.management.academic.domain.valueobject.enums.SubjectEnrollmentStatus;
import org.school.management.course.domain.model.StudentCourseSubject;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentCourseSubjectRepository {
    StudentCourseSubject save(StudentCourseSubject studentCourseSubject);

    Optional<StudentCourseSubject> findById(StudentCourseSubjectId id);

    List<StudentCourseSubject> findByEnrollment(UUID enrollmentId);

    Optional<StudentCourseSubject> findByEnrollmentAndCourseSubject(UUID enrollmentId, UUID courseSubjectId);

    List<StudentCourseSubject> findByEnrollmentAndStatus(UUID enrollmentId, SubjectEnrollmentStatus status);

    List<StudentCourseSubject> findWithLowAttendance(UUID enrollmentId, BigDecimal minPercentage);

    long countActiveStudents(UUID courseSubjectId);
}
