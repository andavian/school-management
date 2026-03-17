package org.school.management.grades.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.repository.PeriodGradeRepository;
import org.school.management.grades.domain.valueobject.PeriodGradeId;
import org.school.management.grades.infrastructure.persistence.mapper.PeriodGradePersistenceMapper;
import org.school.management.grades.infrastructure.persistence.repository.PeriodGradeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PeriodGradeRepositoryAdapter implements PeriodGradeRepository {

    private final PeriodGradeJpaRepository jpaRepository;
    private final PeriodGradePersistenceMapper mapper;

    @Override
    public PeriodGrade save(PeriodGrade periodGrade) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(periodGrade))
        );
    }

    @Override
    public Optional<PeriodGrade> findById(PeriodGradeId periodGradeId) {
        return jpaRepository.findById(periodGradeId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<PeriodGrade> findByStudentCourseSubjectAndPeriod(
            StudentCourseSubjectId studentCourseSubjectId,
            PeriodId periodId) {
        return jpaRepository
                .findByStudentCourseSubjectIdAndPeriodId(
                        studentCourseSubjectId.value(),
                        periodId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<PeriodGrade> findByStudentCourseSubject(
            StudentCourseSubjectId studentCourseSubjectId) {
        return jpaRepository
                .findByStudentCourseSubjectId(studentCourseSubjectId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PeriodGrade> findUnvalidatedByPeriod(PeriodId periodId) {
        return jpaRepository
                .findUnvalidatedByPeriod(periodId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PeriodGrade> findByEnrollment(UUID enrollmentId) {
        // Query comentada en JpaRepository hasta implementar course/
        // Retorna lista vacía como placeholder seguro
        return List.of();
    }
}