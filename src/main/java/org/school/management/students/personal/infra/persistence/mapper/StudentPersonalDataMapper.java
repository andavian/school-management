package org.school.management.students.personal.infra.persistence.mapper;

import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.infra.persistence.entity.StudentPersonalDataEntity;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface StudentPersonalDataMapper {

    StudentPersonalDataMapper INSTANCE = Mappers.getMapper(StudentPersonalDataMapper.class);

    // === Entity -> Domain Model ===

    @Mapping(source = "studentId", target = "studentId", qualifiedByName = "toStudentPersonalDataId")
    @Mapping(source = "userId", target = "userId", qualifiedByName = "toUserId")
    @Mapping(target = "dni", qualifiedByName = "toDni")
    @Mapping(target = "fullName", qualifiedByName = "toFullName")
    @Mapping(target = "birthPlaceId", qualifiedByName = "toPlaceId")
    @Mapping(target = "residencePlaceId", qualifiedByName = "toPlaceId")
    @Mapping(target = "address", qualifiedByName = "toAddress")
    @Mapping(source = "phone", target = "phone", qualifiedByName = "toPhoneNumber")
    @Mapping(source = "email", target = "email", qualifiedByName = "toEmail")
    @Mapping(source = "gender", target = "gender", qualifiedByName = "toGender")
    @Mapping(source = "nationality", target = "nationality", qualifiedByName = "toNationality")
    @Mapping(source = "cuil", target = "cuil", qualifiedByName = "toCuil")
    StudentPersonalData toDomain(StudentPersonalDataEntity entity);

    // === Domain Model -> Entity ===

    @Mapping(source = "studentId.value", target = "studentId")
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "dni.value", target = "dni")
    @Mapping(source = "fullName.firstName", target = "firstName")
    @Mapping(source = "fullName.lastName", target = "lastName")
    @Mapping(source = "birthPlaceId.value", target = "birthPlaceId")
    @Mapping(source = "residencePlaceId.value", target = "residencePlaceId")
    @Mapping(source = "address.street", target = "addressStreet")
    @Mapping(source = "address.number", target = "addressNumber")
    @Mapping(source = "address.floor", target = "addressFloor")
    @Mapping(source = "address.apartment", target = "addressApartment")
    @Mapping(source = "address.postalCode", target = "postalCode")
    @Mapping(source = "phone.value", target = "phone")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "gender", target = "gender", qualifiedByName = "fromGender")
    @Mapping(source = "nationality.value", target = "nationality")
    @Mapping(source = "cuil.value", target = "cuil")
    StudentPersonalDataEntity toEntity(StudentPersonalData domain);

    // === Updates (partial mapping) ===

    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "dni", ignore = true)
    @Mapping(target = "cuil", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "birthPlaceId", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "nationality", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntityFromDomain(@MappingTarget StudentPersonalDataEntity entity, StudentPersonalData domain);

    // === Helper methods ===

    @Named("toStudentPersonalDataId")
    default StudentPersonalDataId mapToStudentPersonalDataId(UUID uuid) {
        return uuid != null ? StudentPersonalDataId.from(uuid) : null;
    }

    @Named("toUserId")
    default UserId mapToUserId(UUID uuid) {
        return uuid != null ? UserId.from(uuid) : null;
    }

    @Named("toDni")
    default Dni mapToDni(String value) {
        return value != null ? Dni.of(value) : null;
    }

    @Named("toCuil")
    default Cuil mapToCuil(String value) {
        return value != null ? Cuil.of(value) : null;
    }

    @Named("toFullName")
    default FullName mapToFullName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return null;
        }
        return FullName.of(firstName, lastName);
    }

    @Named("toAddress")
    default Address mapToAddress(String street, String number, String floor, String apartment, String postalCode, UUID placeId) {
        if (street == null || number == null || placeId == null) {
            return null;
        }
        return new Address(street, number, floor, apartment, ResidencePlaceId.of(placeId), postalCode);
    }

    @Named("toPhoneNumber")
    default PhoneNumber mapToPhoneNumber(String value) {
        return value != null ? PhoneNumber.of(value) : null;
    }

    @Named("toEmail")
    default Email mapToEmail(String value) {
        return value != null ? Email.of(value) : null;
    }

    @Named("toGender")
    default Gender mapToGender(org.school.management.students.personal.infra.persistence.entity.GenderEntity entityGender) {
        if (entityGender == null) {
            return null;
        }
        return switch (entityGender) {
            case MALE -> Gender.MALE;
            case FEMALE -> Gender.FEMALE;
            case OTHER -> Gender.OTHER;
        };
    }

    @Named("toNationality")
    default Nationality mapToNationality(String value) {
        return value != null ? Nationality.of(value) : null;
    }

    @Named("toPlaceId")
    default PlaceId mapToPlaceId(UUID uuid) {
        return uuid != null ? PlaceId.of(uuid) : null;
    }

    @Named("fromGender")
    default org.school.management.students.personal.infra.persistence.entity.GenderEntity mapFromGender(Gender gender) {
        if (gender == null) {
            return null;
        }
        return switch (gender) {
            case MALE -> org.school.management.students.personal.infra.persistence.entity.GenderEntity.MALE;
            case FEMALE -> org.school.management.students.personal.infra.persistence.entity.GenderEntity.FEMALE;
            case OTHER -> org.school.management.students.personal.infra.persistence.entity.GenderEntity.OTHER;
        };
    }
}