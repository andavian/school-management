// org.school.management.attendance.infrastructure.persistence.mapper.AttendanceSummaryPersistenceMapper
package org.school.management.attendance.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.attendance.infrastructure.persistence.entity.AttendanceSummaryEntity;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttendanceSummaryPersistenceMapper {

    default AttendanceSummaryEntity toEntity(AttendanceSummary domain) {
        AttendanceSummaryEntity entity = new AttendanceSummaryEntity();
        entity.setAttendanceSummaryId(domain.getAttendanceSummaryId().value());
        entity.setStudentCourseSubjectId(domain.getStudentCourseSubjectId().value());
        entity.setCourseSubjectId(domain.getCourseSubjectId().value());
        entity.setPeriodId(domain.getPeriodId().value());
        entity.setTotalClasses(domain.getTotalClasses());
        entity.setPresentCount(domain.getPresentCount());
        entity.setAbsentCount(domain.getAbsentCount());
        entity.setJustifiedCount(domain.getJustifiedCount());
        entity.setLateCount(domain.getLateCount());
        entity.setWithdrawnCount(domain.getWithdrawnCount());
        entity.setWeightedAbsences(domain.getWeightedAbsences());
        entity.setAttendancePercentage(domain.getAttendancePercentage());
        entity.setAtRisk(domain.isAtRisk());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default AttendanceSummary toDomain(AttendanceSummaryEntity entity) {
        return AttendanceSummary.builder()
                .attendanceSummaryId(AttendanceSummaryId.of(entity.getAttendanceSummaryId()))
                .studentCourseSubjectId(StudentCourseSubjectId.of(entity.getStudentCourseSubjectId()))
                .courseSubjectId(CourseSubjectId.of(entity.getCourseSubjectId()))
                .periodId(PeriodId.of(entity.getPeriodId()))
                .totalClasses(entity.getTotalClasses())
                .presentCount(entity.getPresentCount())
                .absentCount(entity.getAbsentCount())
                .justifiedCount(entity.getJustifiedCount())
                .lateCount(entity.getLateCount())
                .withdrawnCount(entity.getWithdrawnCount())
                .weightedAbsences(entity.getWeightedAbsences())
                .attendancePercentage(entity.getAttendancePercentage())
                .atRisk(entity.isAtRisk())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}