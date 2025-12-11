package org.school.management.academic.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.school.management.academic.application.dto.response.*;
import org.school.management.academic.domain.model.*;
import org.school.management.academic.domain.valueobject.*;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.enums.Shift;
import org.school.management.academic.domain.valueobject.ids.*;

import java.util.UUID;

// ============================================================================
// APPLICATION MAPPER
// Domain Models → Response DTOs
// ============================================================================

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AcademicApplicationMapper {

    // ========================================================================
    // ACADEMIC YEAR
    // ========================================================================

    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "status", source = "status")
    AcademicYearResponse toAcademicYearResponse(AcademicYear domain);

    // Custom mappings
    default String mapAcademicYearId(AcademicYearId id) {
        return id != null ? id.getValue().toString() : null;
    }

    default Integer mapYear(Year year) {
        return year != null ? year.getValue() : null;
    }

    default String mapStatus(AcademicYearStatus status) {
        return status != null ? status.name() : null;
    }


    @Mapping(target = "orientationId", source = "orientationId")
    OrientationResponse toOrientationResponse(Orientation domain);

    default String mapOrientationId(OrientationId id) {
        return id != null ? id.getValue().toString() : null;
    }

    // ========================================================================
    // GRADE LEVEL
    // ========================================================================

    @Mapping(target = "gradeLevelId", source = "gradeLevelId")
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "yearLevel", source = "yearLevel.value")
    @Mapping(target = "division", source = "division.value")
    @Mapping(target = "displayName", expression = "java(domain.getDisplayName())")
    @Mapping(target = "orientationId", source = "orientationId")
    @Mapping(target = "orientationName", ignore = true)  // Must be populated separately if needed
    @Mapping(target = "fullDisplayName", ignore = true)  // Must be populated separately if needed
    @Mapping(target = "shift", source = "shift")
    @Mapping(target = "homeroomTeacherId", source = "homeroomTeacherId")
    GradeLevelResponse toGradeLevelResponse(GradeLevel domain);

    default String mapGradeLevelId(GradeLevelId id) {
        return id != null ? id.getValue().toString() : null;
    }

    default String mapShift(Shift shift) {
        return shift != null ? shift.name() : null;
    }

    default String mapUUID(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    // ========================================================================
    // SUBJECT
    // ========================================================================

    @Mapping(target = "subjectId", source = "subjectId")
    @Mapping(target = "yearLevel", source = "yearLevel.value")
    @Mapping(target = "orientationId", source = "orientationId")
    @Mapping(target = "orientationName", ignore = true)  // Must be populated separately if needed
    @Mapping(target = "isCommonSubject", expression = "java(domain.isCommonSubject())")
    SubjectResponse toSubjectResponse(Subject domain);

    default String mapSubjectId(SubjectId id) {
        return id != null ? id.getValue().toString() : null;
    }

    default String mapSubjectId(SubjectCode code) {
        return code != null ? code.getValue() : null;
    }

    default Integer mapWheeklyHours(WeeklyHours hours){
        return hours != null ? hours.getValue() : null;
    }

    // ========================================================================
    // STUDY PLAN
    // ========================================================================

    @Mapping(target = "studyPlanId", source = "studyPlanId")
    @Mapping(target = "yearLevel", source = "yearLevel.value")
    @Mapping(target = "orientationId", source = "orientationId")
    @Mapping(target = "orientationName", ignore = true)  // Must be populated separately if needed
    @Mapping(target = "isGeneralPlan", expression = "java(domain.isGeneralPlan())")
    StudyPlanResponse toStudyPlanResponse(StudyPlan domain);

    default String mapStudyPlanId(StudyPlanId id) {
        return id != null ? id.getValue().toString() : null;
    }

    // ========================================================================
    // EVALUATION PERIOD
    // ========================================================================

    @Mapping(target = "periodId", source = "periodId")
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "isInProgress", expression = "java(domain.isInProgress(java.time.LocalDate.now()))")
    EvaluationPeriodResponse toEvaluationPeriodResponse(EvaluationPeriod domain);

    default String mapEvaluationPeriodId(PeriodId id) {
        return id != null ? id.getValue().toString() : null;
    }

    default Integer mapPeriodNumber(PeriodNumber number) {
        return number != null ? number.getValue() : null;
    }

    // ========================================================================
    // QUALIFICATION REGISTRY
    // ========================================================================

    @Mapping(target = "registryId", source = "registryId")
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "year", ignore = true)  // Must be populated separately
    @Mapping(target = "status", source = "status")
    @Mapping(target = "availableFolios", expression = "java(domain.getAvailableFolios())")
    @Mapping(target = "usedFolios", expression = "java(domain.getUsedFolios())")
    @Mapping(target = "usagePercentage", expression = "java(domain.getUsagePercentage())")
    @Mapping(target = "isNearlyFull", expression = "java(domain.isNearlyFull(50))")
    QualificationRegistryResponse toQualificationRegistryResponse(QualificationRegistry domain);

    default String mapQualificationRegistryId(RegistryId id) {
        return id != null ? id.getValue().toString() : null;
    }

    default String mapRegistryStatus(RegistryStatus status) {
        return status != null ? status.name() : null;
    }

    default String mapRegistryNumber(RegistryNumber number) {
        return number != null ? number.getValue() : null;
    }
}