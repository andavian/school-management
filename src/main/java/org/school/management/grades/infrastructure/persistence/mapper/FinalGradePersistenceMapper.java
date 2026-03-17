package org.school.management.grades.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.valueobject.FinalGradeId;
import org.school.management.grades.infrastructure.persistence.entity.FinalGradeEntity;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinalGradePersistenceMapper {

    default FinalGradeEntity toEntity(FinalGrade domain) {
        FinalGradeEntity entity = new FinalGradeEntity();
        entity.setFinalGradeId(domain.getFinalGradeId().value());
        entity.setStudentCourseSubjectId(domain.getStudentCourseSubjectId().value());
        entity.setAcademicYearId(domain.getAcademicYearId().value());
        entity.setPeriodAverage(domain.getPeriodAverage());
        entity.setFinalExamGrade(domain.getFinalExamGrade());
        entity.setFinalGrade(domain.getFinalGrade());
        entity.setStatus(domain.getStatus());
        entity.setValidated(domain.isValidated());
        entity.setValidatedBy(domain.getValidatedBy());
        entity.setValidatedAt(domain.getValidatedAt());
        entity.setRecordedInRegistry(domain.isRecordedInRegistry());
        entity.setRegistryId(
                domain.getRegistryId() != null ? domain.getRegistryId().value() : null);
        entity.setFolioNumber(domain.getFolioNumber());
        entity.setRecordedAt(domain.getRecordedAt());
        entity.setObservations(domain.getObservations());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default FinalGrade toDomain(FinalGradeEntity entity) {
        return FinalGrade.builder()
                .finalGradeId(FinalGradeId.of(entity.getFinalGradeId()))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.from(entity.getStudentCourseSubjectId()))
                .academicYearId(AcademicYearId.of(entity.getAcademicYearId()))
                .periodAverage(entity.getPeriodAverage())
                .finalExamGrade(entity.getFinalExamGrade())
                .finalGrade(entity.getFinalGrade())
                .status(entity.getStatus())
                .isValidated(entity.isValidated())
                .validatedBy(entity.getValidatedBy())
                .validatedAt(entity.getValidatedAt())
                .recordedInRegistry(entity.isRecordedInRegistry())
                .registryId(
                        entity.getRegistryId() != null
                                ? RegistryId.of(entity.getRegistryId())
                                : null)
                .folioNumber(entity.getFolioNumber())
                .recordedAt(entity.getRecordedAt())
                .observations(entity.getObservations())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
