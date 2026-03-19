package org.school.management.course.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.infrastructure.persistence.mapper.CourseSubjectPersistenceMapper;
import org.school.management.course.infrastructure.persistence.repository.CourseSubjectJpaRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseSubjectRepositoryAdapter implements CourseSubjectRepository {

    private final CourseSubjectJpaRepository jpaRepository;
    private final CourseSubjectPersistenceMapper mapper;

    @Override
    public CourseSubject save(CourseSubject courseSubject) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(courseSubject)));
    }

    @Override
    public Optional<CourseSubject> findById(CourseSubjectId courseSubjectId) {
        return jpaRepository.findById(courseSubjectId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<CourseSubject> findByGradeLevelAndYear(
            GradeLevelId gradeLevelId, AcademicYearId academicYearId) {
        return jpaRepository
                .findByGradeLevelIdAndAcademicYearId(
                        gradeLevelId.value(), academicYearId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CourseSubject> findByTeacherAndYear(
            TeacherId teacherId, AcademicYearId academicYearId) {
        return jpaRepository
                .findByTeacherIdAndAcademicYearId(
                        teacherId.value(), academicYearId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByGradeLevelAndSubjectAndYear(
            GradeLevelId gradeLevelId, SubjectId subjectId, AcademicYearId academicYearId) {
        return jpaRepository.existsByGradeLevelIdAndSubjectIdAndAcademicYearId(
                gradeLevelId.value(), subjectId.value(), academicYearId.value());
    }
}