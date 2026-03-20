// org.school.management.attendance.domain.repository.DailyAttendanceRepository
package org.school.management.attendance.domain.repository;

import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyAttendanceRepository {

    Optional<DailyAttendance> findById(DailyAttendanceId id);

    boolean existsByStudentIdAndDate(StudentPersonalDataId studentId, LocalDate date);

    List<DailyAttendance> findByGradeLevelIdAndDate(GradeLevelId gradeLevelId, LocalDate date);

    List<DailyAttendance> findByStudentIdAndAcademicYearId(StudentPersonalDataId studentId,
                                                           AcademicYearId academicYearId);

    DailyAttendance save(DailyAttendance dailyAttendance);
}