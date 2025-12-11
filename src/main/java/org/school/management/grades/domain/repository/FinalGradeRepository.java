package org.school.management.grades.domain.repository;

import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.valueobject.FinalGradeId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinalGradeRepository {
    FinalGrade save(FinalGrade finalGrade);

    Optional<FinalGrade> findById(FinalGradeId finalGradeId);

    Optional<FinalGrade> findByStudentCourseSubjectAndYear(StudentCourseSubjectId studentCourseSubjectId, AcademicYearId academicYearId);

    List<FinalGrade> findByEnrollmentAndYear(UUID enrollmentId, AcademicYearId academicYearId);

    List<FinalGrade> findUnvalidatedByYear(AcademicYearId academicYearId);

    List<FinalGrade> findPendingRegistryRecord(AcademicYearId academicYearId);

    List<FinalGrade> findByRegistry(RegistryId registryId);
}
