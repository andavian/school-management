package org.school.management.grades.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.valueobject.FinalGradeId;
import org.school.management.grades.infrastructure.persistence.mapper.FinalGradePersistenceMapper;
import org.school.management.grades.infrastructure.persistence.repository.FinalGradeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FinalGradeRepositoryAdapter implements FinalGradeRepository {

    private final FinalGradeJpaRepository jpaRepository;
    private final FinalGradePersistenceMapper mapper;

    @Override
    public FinalGrade save(FinalGrade finalGrade) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(finalGrade))
        );
    }

    @Override
    public Optional<FinalGrade> findById(FinalGradeId finalGradeId) {
        return jpaRepository.findById(finalGradeId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<FinalGrade> findByStudentCourseSubjectAndYear(
            StudentCourseSubjectId studentCourseSubjectId,
            AcademicYearId academicYearId) {
        return jpaRepository
                .findByStudentCourseSubjectIdAndAcademicYearId(
                        studentCourseSubjectId.value(),
                        academicYearId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<FinalGrade> findByEnrollmentAndYear(UUID enrollmentId,
                                                    AcademicYearId academicYearId) {
        // Query comentada en JpaRepository hasta implementar course/
        // Retorna lista vacía como placeholder seguro
        return List.of();
    }

    @Override
    public List<FinalGrade> findUnvalidatedByYear(AcademicYearId academicYearId) {
        return jpaRepository
                .findUnvalidatedByYear(academicYearId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<FinalGrade> findPendingRegistryRecord(AcademicYearId academicYearId) {
        return jpaRepository
                .findPendingRegistryRecord(academicYearId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<FinalGrade> findByRegistry(RegistryId registryId) {
        return jpaRepository
                .findByRegistry(registryId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
