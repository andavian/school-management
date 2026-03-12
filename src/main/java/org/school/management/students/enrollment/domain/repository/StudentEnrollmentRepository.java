package org.school.management.students.enrollment.domain.repository;

import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.util.List;
import java.util.Optional;

public interface StudentEnrollmentRepository {

    Optional<StudentEnrollment> findByEnrollmentId(EnrollmentId enrollmentId);

    Optional<StudentEnrollment> findByStudentIdAndAcademicYearId(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId
    );

    List<StudentEnrollment> findAllByStudentId(StudentPersonalDataId studentId);

    List<StudentEnrollment> findActiveByStudentId(StudentPersonalDataId studentId);

    List<StudentEnrollment> findAllByAcademicYearId(AcademicYearId academicYearId);

    List<StudentEnrollment> findByGradeLevelIdAndAcademicYearId(
            GradeLevelId gradeLevelId,
            AcademicYearId academicYearId
    );

    boolean existsActiveEnrollment(StudentPersonalDataId studentId, AcademicYearId academicYearId);

    boolean existsCompletedEnrollment(StudentPersonalDataId studentId, AcademicYearId academicYearId);

    StudentEnrollment save(StudentEnrollment enrollment);


}