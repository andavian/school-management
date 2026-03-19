package org.school.management.course.domain.repository;

import org.school.management.course.domain.model.StudentCourseSubject;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentCourseSubjectRepository {

    StudentCourseSubject save(StudentCourseSubject studentCourseSubject);

    Optional<StudentCourseSubject> findById(StudentCourseSubjectId id);

    List<StudentCourseSubject> findByEnrollment(UUID enrollmentId);

    Optional<StudentCourseSubject> findByEnrollmentAndCourseSubject(
            UUID enrollmentId,
            UUID courseSubjectId
    );

    List<StudentCourseSubject> findByEnrollmentAndStatus(
            UUID enrollmentId,
            SubjectEnrollmentStatus status
    );

    boolean existsByEnrollmentAndCourseSubject(UUID enrollmentId, UUID courseSubjectId);
}