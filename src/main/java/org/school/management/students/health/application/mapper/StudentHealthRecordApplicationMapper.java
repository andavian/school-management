package org.school.management.students.health.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.students.health.application.dto.response.HealthRecordResponse;
import org.school.management.students.health.domain.model.StudentHealthRecord;

/**
 * Mapper MapStruct: Domain → Application DTO
 *
 * Responsabilidades:
 * - StudentHealthRecord → HealthRecordResponse
 *
 * NO mapea request → domain (eso lo hace el Use Case directamente)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentHealthRecordApplicationMapper {

    @Mapping(target = "healthRecordId",
            expression = "java(record.getHealthRecordId().value())")
    @Mapping(target = "studentId",
            expression = "java(record.getStudentId().value())")
    @Mapping(target = "bloodType",
            expression = "java(record.getBloodType() != null ? record.getBloodType().getDisplayName() : null)")
    @Mapping(target = "emergencyContactFirstName",
            expression = "java(record.getEmergencyContactName().firstName())")
    @Mapping(target = "emergencyContactLastName",
            expression = "java(record.getEmergencyContactName().lastName())")
    @Mapping(target = "emergencyContactFullName",
            expression = "java(record.getEmergencyContactName().firstNameFirst())")
    @Mapping(target = "emergencyContactPhone",
            expression = "java(record.getEmergencyContactPhone().value())")
    @Mapping(target = "hasCompleteHealthInfo",
            expression = "java(record.hasCompleteHealthInfo())")
    @Mapping(target = "hasAllergies",
            expression = "java(record.hasAllergies())")
    @Mapping(target = "hasChronicConditions",
            expression = "java(record.hasChronicConditions())")
    @Mapping(target = "requiresMedication",
            expression = "java(record.requiresMedication())")
    HealthRecordResponse toHealthRecordResponse(StudentHealthRecord record);
}