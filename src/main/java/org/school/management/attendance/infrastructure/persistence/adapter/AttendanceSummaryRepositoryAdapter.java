// org.school.management.attendance.infrastructure.persistence.adapter.AttendanceSummaryRepositoryAdapter
package org.school.management.attendance.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.infrastructure.persistence.mapper.AttendanceSummaryPersistenceMapper;
import org.school.management.attendance.infrastructure.persistence.repository.AttendanceSummaryJpaRepository;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AttendanceSummaryRepositoryAdapter implements AttendanceSummaryRepository {

    private final AttendanceSummaryJpaRepository jpaRepository;
    private final AttendanceSummaryPersistenceMapper mapper;

    @Override
    public Optional<AttendanceSummary> findByStudentCourseSubjectIdAndPeriodId(
            StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId) {
        return jpaRepository
                .findByStudentCourseSubjectIdAndPeriodId(
                        studentCourseSubjectId.value(), periodId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<AttendanceSummary> findAtRiskByCourseSubjectIdAndPeriodId(
            CourseSubjectId courseSubjectId, PeriodId periodId) {
        return jpaRepository
                .findByCourseSubjectIdAndPeriodIdAndAtRiskTrue(
                        courseSubjectId.value(), periodId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AttendanceSummary> findByCourseSubjectIdAndPeriodId(
            CourseSubjectId courseSubjectId, PeriodId periodId) {
        return jpaRepository
                .findByCourseSubjectIdAndPeriodId(courseSubjectId.value(), periodId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public AttendanceSummary save(AttendanceSummary summary) {
        var entity = mapper.toEntity(summary);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}