package org.school.management.students.personal.application.mappers;

import org.mapstruct.*;
import org.school.management.students.personal.application.dto.*;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.shared.geography.domain.valueobject.PlaceId;

/**
 * MapStruct Mapper: Domain (StudentPersonalData) ↔ Application DTOs
 *
 * Convierte entre modelos de dominio y DTOs de la capa de aplicación
 * IMPORTANTE: Usa Address del Shared Kernel (record con ResidencePlaceId)
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentPersonalDataMapper {

    // ========== Domain → Response DTO ==========

    /**
     * Convierte StudentPersonalData a StudentResponse completo
     */
    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "dni", source = "dni.value")
    @Mapping(target = "cuil", source = "cuil.value")
    @Mapping(target = "fullName", expression = "java(domain.getFullName().getFullName())")
    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "age", expression = "java(domain.calculateAge())")
    @Mapping(target = "isAdult", expression = "java(domain.isAdult())")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "nationality", source = "nationality.value")
    @Mapping(target = "phone", source = "phone.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "address", expression = "java(mapAddressDto(domain.getAddress(), null))")
    @Mapping(target = "birthPlace", ignore = true) // Se completa en el Use Case con Geography service
    @Mapping(target = "residencePlace", ignore = true) // Se completa en el Use Case con Geography service
    StudentResponse toResponse(StudentPersonalData domain);

    /**
     * Convierte StudentPersonalData a StudentSummaryResponse (para listas)
     */
    @Mapping(target = "studentId", source = "studentId.value")
    @Mapping(target = "dni", source = "dni.value")
    @Mapping(target = "fullName", expression = "java(domain.getFullName().getFullName())")
    @Mapping(target = "age", expression = "java(domain.calculateAge())")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "phone", source = "phone.value")
    StudentSummaryResponse toSummaryResponse(StudentPersonalData domain);

    /**
     * Mapea Address del dominio (Shared Kernel) a AddressDto
     *
     * @param address Address VO del Shared Kernel
     * @param localityName Nombre de la localidad (obtenido de Geography service)
     * @return AddressDto con formato completo
     */
    default StudentResponse.AddressDto mapAddressDto(Address address, String localityName) {
        if (address == null) return null;

        return new StudentResponse.AddressDto(
                address.street(),
                address.number(),
                address.floor(),
                address.apartment(),
                address.residencePlaceId().value(),
                localityName, // Se completa en Use Case
                address.postalCode(),
                localityName != null ? address.toStringFormatted(localityName) : null
        );
    }

    // ========== Request DTO → Domain Value Objects ==========

    /**
     * Extrae FullName desde CreateStudentRequest
     */
    default FullName mapFullName(CreateStudentRequest request) {
        return FullName.of(request.firstName(), request.lastName());
    }

    /**
     * Extrae FullName desde UpdateStudentRequest
     */
    default FullName mapFullName(UpdateStudentRequest request) {
        return FullName.of(request.firstName(), request.lastName());
    }

    /**
     * Extrae Dni desde String
     */
    default Dni mapDni(String dni) {
        return dni != null ? Dni.of(dni) : null;
    }

    /**
     * Extrae Cuil desde String
     */
    default Cuil mapCuil(String cuil) {
        return cuil != null ? Cuil.of(cuil) : null;
    }

    /**
     * Extrae Gender desde String
     */
    default Gender mapGender(String gender) {
        if (gender == null) return null;
        return switch (gender.toUpperCase()) {
            case "MALE" -> Gender.MALE;
            case "FEMALE" -> Gender.FEMALE;
            case "OTHER" -> Gender.OTHER;
            default -> throw new IllegalArgumentException("Invalid gender: " + gender);
        };
    }

    /**
     * Extrae Nationality desde String
     */
    default Nationality mapNationality(String nationality) {
        return nationality != null ? Nationality.of(nationality) : null;
    }

    /**
     * Extrae PhoneNumber desde String
     */
    default PhoneNumber mapPhoneNumber(String phone) {
        return phone != null && !phone.isBlank() ? PhoneNumber.of(phone) : null;
    }

    /**
     * Extrae Email desde String
     */
    default Email mapEmail(String email) {
        return email != null && !email.isBlank() ? Email.of(email) : null;
    }

    /**
     * Extrae PlaceId desde UUID
     */
    default PlaceId mapPlaceId(java.util.UUID uuid) {
        return uuid != null ? PlaceId.of(uuid) : null;
    }

    /**
     * Extrae ResidencePlaceId desde UUID
     */
    default ResidencePlaceId mapResidencePlaceId(java.util.UUID uuid) {
        return uuid != null ? ResidencePlaceId.of(uuid) : null;
    }


    default Address mapAddress(CreateStudentRequest request) {
        return new Address(
                request.addressStreet(),
                request.addressNumber(),
                request.addressFloor(),
                request.addressApartment(),
                mapResidencePlaceId(request.residencePlaceId()),
                request.postalCode()
        );
    }


    default Address mapAddress(UpdateStudentRequest request) {
        return new Address(
                request.addressStreet(),
                request.addressNumber(),
                request.addressFloor(),
                request.addressApartment(),
                mapResidencePlaceId(request.residencePlaceId()),
                request.postalCode()
        );
    }
}