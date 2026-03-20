package org.school.management.students.health.infrastructure.persistence.mapper;

import org.mapstruct.*;
import org.school.management.shared.person.domain.valueobject.FullName;
import org.school.management.shared.person.domain.valueobject.PhoneNumber;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.valueobject.BloodType;
import org.school.management.students.health.domain.valueobject.HealthRecordId;
import org.school.management.students.health.infrastructure.persistence.entity.StudentHealthRecordEntity;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

/**
 * PersistenceMapper: StudentHealthRecord (Domain) ↔ StudentHealthRecordEntity (JPA)
 *
 * Patrones aplicados:
 * - VOs de un campo → expression="java(...)"
 * - VOs compuestos (FullName, PhoneNumber) → ignore=true en toDomain + @AfterMapping
 * - emergencyContactName: almacenado como "firstName lastName" en BD,
 *   reconstruido separando por primer espacio en @AfterMapping
 * - bloodType: almacenado como nombre del enum (A_POSITIVE), convertido a/desde displayName en use cases
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
imports={
        BloodType.class,
        HealthRecordId.class,
        StudentPersonalDataId.class
})
public interface StudentHealthRecordPersistenceMapper {

    // ── Domain → Entity ──────────────────────────────────────────────────

    @Mapping(target = "healthRecordId",
            expression = "java(domain.getHealthRecordId().value())")
    @Mapping(target = "studentId",
            expression = "java(domain.getStudentId().value())")
    @Mapping(target = "bloodType",
            expression = "java(domain.getBloodType() != null ? domain.getBloodType().name() : null)")
    @Mapping(target = "emergencyContactName",
            expression = "java(domain.getEmergencyContactName().firstNameFirst())")
    @Mapping(target = "emergencyContactPhone",
            expression = "java(domain.getEmergencyContactPhone().value())")
    StudentHealthRecordEntity toEntity(StudentHealthRecord domain);

    // ── Entity → Domain ──────────────────────────────────────────────────

    @Mapping(target = "healthRecordId",
            expression = "java(HealthRecordId.of(entity.getHealthRecordId()))")
    @Mapping(target = "studentId",
            expression = "java(StudentPersonalDataId.of(entity.getStudentId()))")
    @Mapping(target = "bloodType",
            expression = "java(entity.getBloodType() != null ? BloodType.valueOf(entity.getBloodType()) : null)")
    @Mapping(target = "emergencyContactName",  ignore = true)
    @Mapping(target = "emergencyContactPhone", ignore = true)
    StudentHealthRecord toDomain(StudentHealthRecordEntity entity);

    /**
     * Reconstruye VOs compuestos desde columnas simples.
     *
     * emergencyContactName en BD: "firstName lastName"
     * Estrategia: split por primer espacio — primer token = firstName, resto = lastName.
     * Casos borde: nombre compuesto sin apellido → lastName queda vacío (FullName.of lo acepta).
     */
    @AfterMapping
    default void buildCompositeValueObjects(StudentHealthRecordEntity entity,
                                            @MappingTarget StudentHealthRecord.StudentHealthRecordBuilder builder) {

        String fullContactName = entity.getEmergencyContactName();
        if (fullContactName != null && !fullContactName.isBlank()) {
            String[] parts = fullContactName.trim().split("\\s+", 2);
            String firstName = parts[0];
            String lastName  = parts.length > 1 ? parts[1] : "";
            builder.emergencyContactName(FullName.of(firstName, lastName));
        }

        if (entity.getEmergencyContactPhone() != null) {
            builder.emergencyContactPhone(PhoneNumber.of(entity.getEmergencyContactPhone()));
        }
    }
}