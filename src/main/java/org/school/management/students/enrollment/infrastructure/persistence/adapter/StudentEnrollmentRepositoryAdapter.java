package org.school.management.students.enrollment.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;
import org.school.management.students.enrollment.infrastructure.persistence.entity.StudentEnrollmentEntity;
import org.school.management.students.enrollment.infrastructure.persistence.mapper.StudentEnrollmentPersistenceMapper;
import org.school.management.students.enrollment.infrastructure.persistence.repository.StudentEnrollmentJpaRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudentEnrollmentRepositoryAdapter implements StudentEnrollmentRepository {

    private final StudentEnrollmentJpaRepository jpaRepository;
    private final StudentEnrollmentPersistenceMapper mapper;

    @Override
    public Optional<StudentEnrollment> findByEnrollmentId(EnrollmentId enrollmentId) {
        return jpaRepository.findById(enrollmentId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<StudentEnrollment> findByStudentIdAndAcademicYearId(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId) {
        return jpaRepository
                .findByStudentIdAndAcademicYearId(studentId.value(), academicYearId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<StudentEnrollment> findAllByStudentId(StudentPersonalDataId studentId) {
        return jpaRepository.findAllByStudentId(studentId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<StudentEnrollment> findActiveByStudentId(StudentPersonalDataId studentId) {
        return jpaRepository.findActiveByStudentId(studentId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<StudentEnrollment> findAllByAcademicYearId(AcademicYearId academicYearId) {
        return jpaRepository.findAllByAcademicYearId(academicYearId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<StudentEnrollment> findByGradeLevelIdAndAcademicYearId(
            GradeLevelId gradeLevelId,
            AcademicYearId academicYearId) {
        return jpaRepository
                .findByGradeLevelIdAndAcademicYearId(gradeLevelId.value(), academicYearId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsActiveEnrollment(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId) {
        return jpaRepository.existsActiveEnrollment(studentId.value(), academicYearId.value());
    }

    @Override
    public boolean existsCompletedEnrollment(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId) {
        return jpaRepository.existsCompletedEnrollment(studentId.value(), academicYearId.value());
    }

    @Override
    public StudentEnrollment save(StudentEnrollment enrollment) {
        StudentEnrollmentEntity entity = mapper.toEntity(enrollment);
        StudentEnrollmentEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}