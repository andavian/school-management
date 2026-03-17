package org.school.management.grades.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.valueobject.EvaluationId;
import org.school.management.grades.domain.valueobject.EvaluationTypeId;
import org.school.management.grades.infrastructure.persistence.entity.EvaluationEntity;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluationPersistenceMapper {

    default EvaluationEntity toEntity(Evaluation domain) {
        EvaluationEntity entity = new EvaluationEntity();
        entity.setEvaluationId(domain.getEvaluationId().value());
        entity.setStudentCourseSubjectId(domain.getStudentCourseSubjectId().value());
        entity.setPeriodId(domain.getPeriodId().value());
        entity.setEvaluationTypeId(domain.getEvaluationTypeId().value());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setEvaluationDate(domain.getEvaluationDate());
        entity.setGrade(domain.getGrade());
        entity.setMaxGrade(domain.getMaxGrade());
        entity.setStatus(domain.getStatus());
        entity.setValidated(domain.isValidated());
        entity.setValidatedBy(domain.getValidatedBy());
        entity.setValidatedAt(domain.getValidatedAt());
        entity.setTeacherObservations(domain.getTeacherObservations());
        entity.setAdminNotes(domain.getAdminNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        return entity;
    }

    default Evaluation toDomain(EvaluationEntity entity) {
        return Evaluation.builder()
                .evaluationId(EvaluationId.of(entity.getEvaluationId()))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.from(entity.getStudentCourseSubjectId()))
                .periodId(PeriodId.of(entity.getPeriodId()))
                .evaluationTypeId(EvaluationTypeId.of(entity.getEvaluationTypeId()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .evaluationDate(entity.getEvaluationDate())
                .grade(entity.getGrade())
                .maxGrade(entity.getMaxGrade())
                .status(entity.getStatus())
                .isValidated(entity.isValidated())
                .validatedBy(entity.getValidatedBy())
                .validatedAt(entity.getValidatedAt())
                .teacherObservations(entity.getTeacherObservations())
                .adminNotes(entity.getAdminNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}