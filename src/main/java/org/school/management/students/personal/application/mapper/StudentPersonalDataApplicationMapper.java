package org.school.management.students.personal.application.mapper;

import org.mapstruct.*;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.dto.response.StudentSummaryResponse;
import org.school.management.students.personal.domain.model.StudentPersonalData;

/**
 * Mapper MapStruct: Domain → Application DTOs
 *
 * Responsabilidades:
 * - StudentPersonalData → StudentResponse
 * - StudentPersonalData → StudentSummaryResponse
 *
 * NO mapea request → domain (eso lo hace el Use Case via StudentPersonalData.create())
 * NO accede a Geography — los PlaceResponse se resuelven en el Use Case y se pasan como parámetros
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentPersonalDataApplicationMapper {

    // ── StudentPersonalData → StudentResponse ─────────────────────────────

    @Mapping(target = "studentId",      expression = "java(student.getStudentId().value())")
    @Mapping(target = "userId",         expression = "java(student.getUserId().value())")
    @Mapping(target = "dni",            expression = "java(student.getDni().value())")
    @Mapping(target = "cuil",           expression = "java(student.getCuil().formatted())")
    @Mapping(target = "firstName",      expression = "java(student.getFullName().firstName())")
    @Mapping(target = "lastName",       expression = "java(student.getFullName().lastName())")
    @Mapping(target = "fullName",       expression = "java(student.getFullName().fullName())")
    @Mapping(target = "age",            expression = "java(student.calculateAge())")
    @Mapping(target = "isAdult",        expression = "java(student.isAdult())")
    @Mapping(target = "gender",         expression = "java(student.getGender().name())")
    @Mapping(target = "nationality",    expression = "java(student.getNationality().value())")
    @Mapping(target = "phone",          expression = "java(student.getPhone() != null ? student.getPhone().value() : null)")
    @Mapping(target = "email",          expression = "java(student.getEmail() != null ? student.getEmail().value() : null)")
    @Mapping(target = "address",        source = "student", qualifiedByName = "toAddressResponse")
    @Mapping(target = "birthPlace",     source = "birthPlaceResponse")
    @Mapping(target = "residencePlace", source = "residencePlaceResponse")
    @Mapping(target = "createdAt",      source = "student.createdAt")
    @Mapping(target = "updatedAt",      source = "student.updatedAt")
    StudentResponse toStudentResponse(
            StudentPersonalData student,
            StudentResponse.PlaceResponse birthPlaceResponse,
            StudentResponse.PlaceResponse residencePlaceResponse
    );

    // ── StudentPersonalData → StudentSummaryResponse ──────────────────────

    @Mapping(target = "studentId",  expression = "java(student.getStudentId().value())")
    @Mapping(target = "dni",        expression = "java(student.getDni().value())")
    @Mapping(target = "fullName",   expression = "java(student.getFullName().fullName())")
    @Mapping(target = "age",        expression = "java(student.calculateAge())")
    @Mapping(target = "email",      expression = "java(student.getEmail() != null ? student.getEmail().value() : null)")
    @Mapping(target = "phone",      expression = "java(student.getPhone() != null ? student.getPhone().value() : null)")
    StudentSummaryResponse toStudentSummaryResponse(StudentPersonalData student);

    // ── Named mappings ────────────────────────────────────────────────────

    @Named("toAddressResponse")
    default StudentResponse.AddressResponse toAddressResponse(StudentPersonalData student) {
        var address = student.getAddress();
        if (address == null) return null;

        // formatted requiere el nombre de la localidad resuelto externamente.
        // En este mapper no tenemos acceso a Geography, así que se deja null.
        // El Use Case que necesite formatted debe llamar a toStudentResponse()
        // pasando PlaceResponse ya construido con localityName.
        String formatted = null;

        return new StudentResponse.AddressResponse(
                address.street(),
                address.number(),
                address.floor(),
                address.apartment(),
                address.postalCode(),
                address.placeId().value(),
                formatted
        );
    }
}