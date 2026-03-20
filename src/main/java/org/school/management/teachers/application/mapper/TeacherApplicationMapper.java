package org.school.management.teachers.application.mapper;

import org.mapstruct.*;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.application.dto.response.TeacherSummaryResponse;
import org.school.management.teachers.domain.model.Teacher;

/**
 * Mapper MapStruct: Teacher (domain) → Application DTOs.
 *
 * Mismo patrón que StudentPersonalDataApplicationMapper:
 * — PlaceResponse se resuelven en el Use Case y se pasan como parámetros.
 * — NO accede a Geography directamente.
 * — NO mapea request → domain (eso lo hace el Use Case via Teacher.create()).
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeacherApplicationMapper {

    // ── Teacher → TeacherResponse ─────────────────────────────────────────

    @Mapping(target = "teacherId",        expression = "java(teacher.getTeacherId().value())")
    @Mapping(target = "userId",           expression = "java(teacher.getUserId().value())")
    @Mapping(target = "firstName",        expression = "java(teacher.getFullName().firstName())")
    @Mapping(target = "lastName",         expression = "java(teacher.getFullName().lastName())")
    @Mapping(target = "fullName",         expression = "java(teacher.getFullName().firstNameFirst())")
    @Mapping(target = "dni",              expression = "java(teacher.getDni().value())")
    @Mapping(target = "cuil",             expression = "java(teacher.getCuil().formatted())")
    @Mapping(target = "email",            expression = "java(teacher.getEmail().value())")
    @Mapping(target = "gender",           expression = "java(teacher.getGender() != null ? teacher.getGender().name() : null)")
    @Mapping(target = "nationality",      expression = "java(teacher.getNationality() != null ? teacher.getNationality().value() : null)")
    @Mapping(target = "phone",            expression = "java(teacher.getPhone() != null ? teacher.getPhone().value() : null)")
    @Mapping(target = "address",          source = "teacher", qualifiedByName = "toAddressResponse")
    @Mapping(target = "birthPlace",       source = "birthPlaceResponse")
    @Mapping(target = "residencePlace",   source = "residencePlaceResponse")
    @Mapping(target = "specialization",   expression = "java(teacher.getSpecialization() != null ? teacher.getSpecialization().value() : null)")
    @Mapping(target = "teachingLicense",  source = "teacher.teachingLicense")
    @Mapping(target = "hireDate",         source = "teacher.hireDate")
    @Mapping(target = "employmentStatus", source = "teacher.employmentStatus")
    @Mapping(target = "employmentType",   source = "teacher.employmentType")
    @Mapping(target = "active",           source = "teacher.active")
    @Mapping(target = "pendingActivation",expression = "java(teacher.isPendingActivation())")
    @Mapping(target = "activatedAt",      source = "teacher.activatedAt")
    @Mapping(target = "createdAt",        source = "teacher.createdAt")
    @Mapping(target = "updatedAt",        source = "teacher.updatedAt")
    TeacherResponse toTeacherResponse(
            Teacher teacher,
            TeacherResponse.PlaceResponse birthPlaceResponse,
            TeacherResponse.PlaceResponse residencePlaceResponse
    );

    // ── Teacher → TeacherSummaryResponse ──────────────────────────────────

    @Mapping(target = "teacherId",        expression = "java(teacher.getTeacherId().asString())")
    @Mapping(target = "firstName",        expression = "java(teacher.getFullName().firstName())")
    @Mapping(target = "lastName",         expression = "java(teacher.getFullName().lastName())")
    @Mapping(target = "fullName",         expression = "java(teacher.getFullName().firstNameFirst())")
    @Mapping(target = "dni",              expression = "java(teacher.getDni().value())")
    @Mapping(target = "email",            expression = "java(teacher.getEmail().value())")
    @Mapping(target = "phone",            expression = "java(teacher.getPhone() != null ? teacher.getPhone().value() : null)")
    @Mapping(target = "specialization",   expression = "java(teacher.getSpecialization() != null ? teacher.getSpecialization().value() : null)")
    @Mapping(target = "employmentStatus", source = "employmentStatus")
    @Mapping(target = "employmentType",   source = "employmentType")
    @Mapping(target = "active",           source = "active")
    TeacherSummaryResponse toTeacherSummaryResponse(Teacher teacher);

    // ── Named mappings ─────────────────────────────────────────────────────

    @Named("toAddressResponse")
    default TeacherResponse.AddressResponse toAddressResponse(Teacher teacher) {
        var address = teacher.getAddress();
        if (address == null) return null;

        return new TeacherResponse.AddressResponse(
                address.street(),
                address.number(),
                address.floor(),
                address.apartment(),
                address.postalCode(),
                address.placeId().value()
        );
    }
}