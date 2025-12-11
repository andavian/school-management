package org.school.management.grades.domain.repository;

import org.school.management.grades.domain.model.Evaluation;
import org.school.management.academic.domain.valueobject.ids.EvaluationId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository {
    Evaluation save(Evaluation evaluation);

    Optional<Evaluation> findById(EvaluationId evaluationId);

    List<Evaluation> findByStudentCourseSubject(StudentCourseSubjectId studentCourseSubjectId);

    List<Evaluation> findByStudentCourseSubjectAndPeriod(StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId);

    List<Evaluation> findValidatedEvaluations(StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId);

    List<Evaluation> findPendingValidationByTeacher(UUID teacherId);
}
