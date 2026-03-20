// org.school.management.attendance.infrastructure.persistence.repository.CourseAttendanceJpaRepository
package org.school.management.attendance.infrastructure.persistence.repository;

import org.school.management.attendance.infrastructure.persistence.entity.CourseAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CourseAttendanceJpaRepository extends JpaRepository<CourseAttendanceEntity, UUID> {

    boolean existsByStudentCourseSubjectIdAndClassDate(UUID studentCourseSubjectId, LocalDate classDate);

    List<CourseAttendanceEntity> findByStudentCourseSubjectIdAndPeriodId(UUID studentCourseSubjectId,
                                                                         UUID periodId);

    List<CourseAttendanceEntity> findByCourseSubjectIdAndClassDate(UUID courseSubjectId, LocalDate classDate);
}