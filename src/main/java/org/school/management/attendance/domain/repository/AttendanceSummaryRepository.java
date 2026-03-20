// org.school.management.attendance.domain.repository.AttendanceSummaryRepository
package org.school.management.attendance.domain.repository;

import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.util.List;
import java.util.Optional;

public interface AttendanceSummaryRepository {

    Optional<AttendanceSummary> findByStudentCourseSubjectIdAndPeriodId(
            StudentCourseSubjectId studentCourseSubjectId, PeriodId periodId);

    List<AttendanceSummary> findAtRiskByCourseSubjectIdAndPeriodId(
            CourseSubjectId courseSubjectId, PeriodId periodId);

    List<AttendanceSummary> findByCourseSubjectIdAndPeriodId(
            CourseSubjectId courseSubjectId, PeriodId periodId);

    AttendanceSummary save(AttendanceSummary summary);
}