package org.school.management.grades.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.valueobject.PeriodGradeId;
import org.school.management.grades.infrastructure.persistence.entity.PeriodGradeEntity;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PeriodGradePersistenceMapper {

    default PeriodGradeEntity toEntity(PeriodGrade domain) {
        PeriodGradeEntity entity = new PeriodGradeEntity();
        entity.setPeriodGradeId(domain.getPeriodGradeId().value());
        entity.setStudentCourseSubjectId(domain.getStudentCourseSubjectId().value());
        entity.setPeriodId(domain.getPeriodId().value());
        entity.setAverageGrade(domain.getAverageGrade());
        entity.setAdjustedGrade(domain.getAdjustedGrade());
        entity.setFinalPeriodGrade(domain.getFinalPeriodGrade());
        entity.setPassed(domain.getIsPassed());
        entity.setValidated(domain.isValidated());
        entity.setValidatedBy(domain.getValidatedBy());
        entity.setValidatedAt(domain.getValidatedAt());
        entity.setObservations(domain.getObservations());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default PeriodGrade toDomain(PeriodGradeEntity entity) {
        return PeriodGrade.builder()
                .periodGradeId(PeriodGradeId.of(entity.getPeriodGradeId()))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.from(entity.getStudentCourseSubjectId()))
                .periodId(PeriodId.of(entity.getPeriodId()))
                .averageGrade(entity.getAverageGrade())
                .adjustedGrade(entity.getAdjustedGrade())
                .finalPeriodGrade(entity.getFinalPeriodGrade())
                .isPassed(entity.getPassed())
                .isValidated(entity.isValidated())
                .validatedBy(entity.getValidatedBy())
                .validatedAt(entity.getValidatedAt())
                .observations(entity.getObservations())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}