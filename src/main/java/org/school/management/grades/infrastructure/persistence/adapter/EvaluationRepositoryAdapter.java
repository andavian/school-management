package org.school.management.grades.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.school.management.grades.domain.valueobject.EvaluationId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.infrastructure.persistence.mapper.EvaluationPersistenceMapper;
import org.school.management.grades.infrastructure.persistence.repository.EvaluationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluationRepositoryAdapter implements EvaluationRepository {

    private final EvaluationJpaRepository jpaRepository;
    private final EvaluationPersistenceMapper mapper;

    @Override
    public Evaluation save(Evaluation evaluation) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(evaluation))
        );
    }

    @Override
    public Optional<Evaluation> findById(EvaluationId evaluationId) {
        return jpaRepository.findById(evaluationId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Evaluation> findByStudentCourseSubject(
            StudentCourseSubjectId studentCourseSubjectId) {
        return jpaRepository
                .findByStudentCourseSubjectId(studentCourseSubjectId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Evaluation> findByStudentCourseSubjectAndPeriod(
            StudentCourseSubjectId studentCourseSubjectId,
            PeriodId periodId) {
        return jpaRepository
                .findByStudentCourseSubjectAndPeriod(
                        studentCourseSubjectId.value(),
                        periodId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Evaluation> findValidatedEvaluations(
            StudentCourseSubjectId studentCourseSubjectId,
            PeriodId periodId) {
        return jpaRepository
                .findValidatedEvaluations(
                        studentCourseSubjectId.value(),
                        periodId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Evaluation> findPendingValidationByTeacher(UUID teacherId) {
        return jpaRepository
                .findPendingValidationByTeacher(teacherId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}