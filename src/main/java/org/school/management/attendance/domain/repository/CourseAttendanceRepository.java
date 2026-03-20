// org.school.management.attendance.domain.repository.CourseAttendanceRepository
package org.school.management.attendance.domain.repository;

import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CourseAttendanceRepository {

    Optional<CourseAttendance> findById(CourseAttendanceId id);

    boolean existsByStudentCourseSubjectIdAndClassDate(StudentCourseSubjectId studentCourseSubjectId,
                                                       LocalDate classDate);

    List<CourseAttendance> findAllByStudentCourseSubjectIdAndPeriodId(
            StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId);

    List<CourseAttendance> findByCourseSubjectIdAndDate(CourseSubjectId courseSubjectId, LocalDate date);

    CourseAttendance save(CourseAttendance courseAttendance);
}