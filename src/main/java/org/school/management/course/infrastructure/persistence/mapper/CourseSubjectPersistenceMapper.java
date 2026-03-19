package org.school.management.course.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.infrastructure.persistence.entity.CourseSubjectEntity;
import org.school.management.teachers.domain.valueobject.TeacherId;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseSubjectPersistenceMapper {

    default CourseSubjectEntity toEntity(CourseSubject domain) {
        CourseSubjectEntity entity = new CourseSubjectEntity();
        entity.setCourseSubjectId(domain.getCourseSubjectId().value());
        entity.setGradeLevelId(domain.getGradeLevelId().value());
        entity.setSubjectId(domain.getSubjectId().value());
        entity.setTeacherId(domain.getTeacherId() != null
                ? domain.getTeacherId().value() : null);
        entity.setAcademicYearId(domain.getAcademicYearId().value());
        entity.setScheduleJson(domain.getScheduleJson());
        entity.setClassroom(domain.getClassroom());
        entity.setMinPassingGrade(domain.getMinPassingGrade());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default CourseSubject toDomain(CourseSubjectEntity entity) {
        return CourseSubject.builder()
                .courseSubjectId(CourseSubjectId.of(entity.getCourseSubjectId()))
                .gradeLevelId(GradeLevelId.from(entity.getGradeLevelId()))
                .subjectId(SubjectId.from(entity.getSubjectId()))
                .teacherId(entity.getTeacherId() != null
                        ? TeacherId.from(entity.getTeacherId()) : null)
                .academicYearId(AcademicYearId.from(entity.getAcademicYearId()))
                .scheduleJson(entity.getScheduleJson())
                .classroom(entity.getClassroom())
                .minPassingGrade(entity.getMinPassingGrade())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}