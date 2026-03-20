// org.school.management.attendance.infrastructure.persistence.adapter.CourseAttendanceRepositoryAdapter
package org.school.management.attendance.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.repository.CourseAttendanceRepository;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.attendance.infrastructure.persistence.mapper.CourseAttendancePersistenceMapper;
import org.school.management.attendance.infrastructure.persistence.repository.CourseAttendanceJpaRepository;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseAttendanceRepositoryAdapter implements CourseAttendanceRepository {

    private final CourseAttendanceJpaRepository jpaRepository;
    private final CourseAttendancePersistenceMapper mapper;

    @Override
    public Optional<CourseAttendance> findById(CourseAttendanceId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByStudentCourseSubjectIdAndClassDate(StudentCourseSubjectId studentCourseSubjectId,
                                                              LocalDate classDate) {
        return jpaRepository.existsByStudentCourseSubjectIdAndClassDate(
                studentCourseSubjectId.value(), classDate);
    }

    @Override
    public List<CourseAttendance> findAllByStudentCourseSubjectIdAndPeriodId(
            StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId) {
        return jpaRepository.findByStudentCourseSubjectIdAndPeriodId(
                        studentCourseSubjectId.value(), periodId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CourseAttendance> findByCourseSubjectIdAndDate(CourseSubjectId courseSubjectId,
                                                               LocalDate date) {
        return jpaRepository.findByCourseSubjectIdAndClassDate(courseSubjectId.value(), date)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public CourseAttendance save(CourseAttendance courseAttendance) {
        var entity = mapper.toEntity(courseAttendance);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}