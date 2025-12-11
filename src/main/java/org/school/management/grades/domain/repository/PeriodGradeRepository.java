package org.school.management.grades.domain.repository;

import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.valueobject.PeriodGradeId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeriodGradeRepository {
    PeriodGrade save(PeriodGrade periodGrade);

    Optional<PeriodGrade> findById(PeriodGradeId periodGradeId);

    Optional<PeriodGrade> findByStudentCourseSubjectAndPeriod(StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId);

    List<PeriodGrade> findByStudentCourseSubject(StudentCourseSubjectId studentCourseSubjectId);

    List<PeriodGrade> findUnvalidatedByPeriod(PeriodId periodId);

    List<PeriodGrade> findByEnrollment(UUID enrollmentId);
}
