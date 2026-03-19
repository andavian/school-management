package org.school.management.course.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.course.domain.model.StudentCourseSubject;
import org.school.management.course.domain.repository.StudentCourseSubjectRepository;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;
import org.school.management.course.infrastructure.persistence.mapper.StudentCourseSubjectPersistenceMapper;
import org.school.management.course.infrastructure.persistence.repository.StudentCourseSubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudentCourseSubjectRepositoryAdapter implements StudentCourseSubjectRepository {

    private final StudentCourseSubjectJpaRepository jpaRepository;
    private final StudentCourseSubjectPersistenceMapper mapper;

    @Override
    public StudentCourseSubject save(StudentCourseSubject studentCourseSubject) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(studentCourseSubject)));
    }

    @Override
    public Optional<StudentCourseSubject> findById(StudentCourseSubjectId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<StudentCourseSubject> findByEnrollment(UUID enrollmentId) {
        return jpaRepository.findByEnrollmentId(enrollmentId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<StudentCourseSubject> findByEnrollmentAndCourseSubject(
            UUID enrollmentId, UUID courseSubjectId) {
        return jpaRepository
                .findByEnrollmentIdAndCourseSubjectId(enrollmentId, courseSubjectId)
                .map(mapper::toDomain);
    }

    @Override
    public List<StudentCourseSubject> findByEnrollmentAndStatus(
            UUID enrollmentId, SubjectEnrollmentStatus status) {
        return jpaRepository
                .findByEnrollmentIdAndStatus(enrollmentId, status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEnrollmentAndCourseSubject(UUID enrollmentId, UUID courseSubjectId) {
        return jpaRepository.existsByEnrollmentIdAndCourseSubjectId(enrollmentId, courseSubjectId);
    }
}