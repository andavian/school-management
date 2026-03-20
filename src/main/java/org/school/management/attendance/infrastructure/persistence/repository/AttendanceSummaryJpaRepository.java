// org.school.management.attendance.infrastructure.persistence.repository.AttendanceSummaryJpaRepository
package org.school.management.attendance.infrastructure.persistence.repository;

import org.school.management.attendance.infrastructure.persistence.entity.AttendanceSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceSummaryJpaRepository extends JpaRepository<AttendanceSummaryEntity, UUID> {

    Optional<AttendanceSummaryEntity> findByStudentCourseSubjectIdAndPeriodId(UUID studentCourseSubjectId,
                                                                              UUID periodId);

    List<AttendanceSummaryEntity> findByCourseSubjectIdAndPeriodIdAndAtRiskTrue(UUID courseSubjectId,
                                                                                UUID periodId);

    List<AttendanceSummaryEntity> findByCourseSubjectIdAndPeriodId(UUID courseSubjectId, UUID periodId);
}