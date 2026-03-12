package org.school.management.students.enrollment.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;

/**
 * Mapper de capa Application: domain → response DTO.
 * No tiene INSTANCE estático — componentModel = "spring" genera el bean.
 */
@Mapper(componentModel = "spring")
public interface StudentEnrollmentApplicationMapper {

    @Mapping(target = "enrollmentId",       expression = "java(enrollment.getEnrollmentId().value())")
    @Mapping(target = "studentId",          expression = "java(enrollment.getStudentId().value())")
    @Mapping(target = "academicYearId",     expression = "java(enrollment.getAcademicYearId().value())")
    @Mapping(target = "gradeLevelId",       expression = "java(enrollment.getGradeLevelId().value())")
    @Mapping(target = "withdrawalReasonId", expression = "java(enrollment.getWithdrawalReasonId() != null ? enrollment.getWithdrawalReasonId().value() : null)")
    @Mapping(target = "active",             expression = "java(enrollment.isActive())")
    @Mapping(target = "canReceiveGrades",   expression = "java(enrollment.canReceiveGrades())")
    @Mapping(target = "durationInDays",     expression = "java(enrollment.getDurationInDays())")
    EnrollmentResponse toEnrollmentResponse(StudentEnrollment enrollment);
}