// org.school.management.attendance.infrastructure.persistence.mapper.CourseAttendancePersistenceMapper
package org.school.management.attendance.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.attendance.infrastructure.persistence.entity.CourseAttendanceEntity;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseAttendancePersistenceMapper {

    default CourseAttendanceEntity toEntity(CourseAttendance domain) {
        CourseAttendanceEntity entity = new CourseAttendanceEntity();
        entity.setCourseAttendanceId(domain.getCourseAttendanceId().value());
        entity.setStudentCourseSubjectId(domain.getStudentCourseSubjectId().value());
        entity.setCourseSubjectId(domain.getCourseSubjectId().value());
        entity.setPeriodId(domain.getPeriodId().value());
        entity.setClassDate(domain.getClassDate());
        entity.setStatus(domain.getStatus().name());
        entity.setObservations(domain.getObservations());
        entity.setRecordedByUserId(domain.getRecordedByUserId());
        entity.setCorrectedByUserId(domain.getCorrectedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default CourseAttendance toDomain(CourseAttendanceEntity entity) {
        return CourseAttendance.builder()
                .courseAttendanceId(CourseAttendanceId.of(entity.getCourseAttendanceId()))
                .studentCourseSubjectId(StudentCourseSubjectId.of(entity.getStudentCourseSubjectId()))
                .courseSubjectId(CourseSubjectId.of(entity.getCourseSubjectId()))
                .periodId(PeriodId.of(entity.getPeriodId()))
                .classDate(entity.getClassDate())
                .status(AttendanceStatus.valueOf(entity.getStatus()))
                .observations(entity.getObservations())
                .recordedByUserId(entity.getRecordedByUserId())
                .correctedByUserId(entity.getCorrectedByUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}