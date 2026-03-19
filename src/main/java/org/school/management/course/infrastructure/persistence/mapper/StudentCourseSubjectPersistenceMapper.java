package org.school.management.course.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.course.domain.model.StudentCourseSubject;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.course.infrastructure.persistence.entity.StudentCourseSubjectEntity;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentCourseSubjectPersistenceMapper {

    default StudentCourseSubjectEntity toEntity(StudentCourseSubject domain) {
        StudentCourseSubjectEntity entity = new StudentCourseSubjectEntity();
        entity.setStudentCourseSubjectId(domain.getStudentCourseSubjectId().value());
        entity.setEnrollmentId(domain.getEnrollmentId());
        entity.setCourseSubjectId(domain.getCourseSubjectId());
        entity.setStatus(domain.getStatus());
        entity.setTotalClasses(domain.getTotalClasses());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default StudentCourseSubject toDomain(StudentCourseSubjectEntity entity) {
        return StudentCourseSubject.builder()
                .studentCourseSubjectId(StudentCourseSubjectId.of(entity.getStudentCourseSubjectId()))
                .enrollmentId(entity.getEnrollmentId())
                .courseSubjectId(entity.getCourseSubjectId())
                .status(entity.getStatus())
                .totalClasses(entity.getTotalClasses())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}