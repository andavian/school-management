// org.school.management.attendance.infrastructure.persistence.mapper.DailyAttendancePersistenceMapper
package org.school.management.attendance.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.attendance.infrastructure.persistence.entity.DailyAttendanceEntity;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DailyAttendancePersistenceMapper {

    default DailyAttendanceEntity toEntity(DailyAttendance domain) {
        DailyAttendanceEntity entity = new DailyAttendanceEntity();
        entity.setDailyAttendanceId(domain.getDailyAttendanceId().value());
        entity.setStudentId(domain.getStudentId().value());
        entity.setGradeLevelId(domain.getGradeLevelId().value());
        entity.setAcademicYearId(domain.getAcademicYearId().value());
        entity.setAttendanceDate(domain.getAttendanceDate());
        entity.setStatus(domain.getStatus().name());
        entity.setJustificationReason(domain.getJustificationReason());
        entity.setObservations(domain.getObservations());
        entity.setRecordedByUserId(domain.getRecordedByUserId());
        entity.setCorrectedByUserId(domain.getCorrectedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default DailyAttendance toDomain(DailyAttendanceEntity entity) {
        return DailyAttendance.builder()
                .dailyAttendanceId(DailyAttendanceId.of(entity.getDailyAttendanceId()))
                .studentId(StudentPersonalDataId.of(entity.getStudentId()))
                .gradeLevelId(GradeLevelId.of(entity.getGradeLevelId()))
                .academicYearId(AcademicYearId.of(entity.getAcademicYearId()))
                .attendanceDate(entity.getAttendanceDate())
                .status(AttendanceStatus.valueOf(entity.getStatus()))
                .justificationReason(entity.getJustificationReason())
                .observations(entity.getObservations())
                .recordedByUserId(entity.getRecordedByUserId())
                .correctedByUserId(entity.getCorrectedByUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}