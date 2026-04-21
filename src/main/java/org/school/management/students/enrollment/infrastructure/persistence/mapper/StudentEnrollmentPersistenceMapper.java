package org.school.management.students.enrollment.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.WithdrawalReasonId;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;
import org.school.management.students.enrollment.infrastructure.persistence.entity.StudentEnrollmentEntity;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

/**
 * PersistenceMapper para StudentEnrollment.
 * Todos los VOs son de un solo campo UUID → se resuelven con expression = "java(...)"
 * No requiere @AfterMapping porque no hay VOs compuestos (como FullName o Address).
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
imports = {
EnrollmentId.class,
StudentPersonalDataId.class,
AcademicYearId.class,
GradeLevelId.class,
WithdrawalReasonId.class
        }
)
public interface StudentEnrollmentPersistenceMapper {

    // ── domain → entity ───────────────────────────────────────────────────

    @Mapping(target = "enrollmentId",      expression = "java(domain.getEnrollmentId().value())")
    @Mapping(target = "studentId",         expression = "java(domain.getStudentId().value())")
    @Mapping(target = "academicYearId",    expression = "java(domain.getAcademicYearId().value())")
    @Mapping(target = "gradeLevelId",      expression = "java(domain.getGradeLevelId().value())")
    @Mapping(target = "withdrawalReasonId",
            expression = "java(domain.getWithdrawalReasonId() != null ? domain.getWithdrawalReasonId().value() : null)")
    @Mapping(target = "passed",
            expression = "java(domain.hasPassed() != null ? domain.hasPassed() : null)")
    StudentEnrollmentEntity toEntity(StudentEnrollment domain);

    // ── entity → domain ───────────────────────────────────────────────────

    @Mapping(target = "enrollmentId",
            expression = "java(EnrollmentId.of(entity.getEnrollmentId()))")
    @Mapping(target = "studentId",
            expression = "java(StudentPersonalDataId.of(entity.getStudentId()))")
    @Mapping(target = "academicYearId",
            expression = "java(AcademicYearId.of(entity.getAcademicYearId()))")
    @Mapping(target = "gradeLevelId",
            expression = "java(GradeLevelId.of(entity.getGradeLevelId()))")
    @Mapping(target = "withdrawalReasonId",
            expression = "java(entity.getWithdrawalReasonId() != null ? WithdrawalReasonId.of(entity.getWithdrawalReasonId()) : null)")
    StudentEnrollment toDomain(StudentEnrollmentEntity entity);
}