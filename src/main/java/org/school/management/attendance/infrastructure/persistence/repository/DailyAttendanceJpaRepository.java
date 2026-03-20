// org.school.management.attendance.infrastructure.persistence.repository.DailyAttendanceJpaRepository
package org.school.management.attendance.infrastructure.persistence.repository;

import org.school.management.attendance.infrastructure.persistence.entity.DailyAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyAttendanceJpaRepository extends JpaRepository<DailyAttendanceEntity, UUID> {

    boolean existsByStudentIdAndAttendanceDate(UUID studentId, LocalDate attendanceDate);

    List<DailyAttendanceEntity> findByGradeLevelIdAndAttendanceDate(UUID gradeLevelId,
                                                                    LocalDate attendanceDate);

    List<DailyAttendanceEntity> findByStudentIdAndAcademicYearId(UUID studentId, UUID academicYearId);
}