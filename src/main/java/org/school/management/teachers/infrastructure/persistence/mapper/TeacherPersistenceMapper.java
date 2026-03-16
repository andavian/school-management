package org.school.management.teachers.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachers.domain.valueobject.TeacherSpecialization;
import org.school.management.teachers.infrastructure.persistence.entity.TeacherEntity;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeacherPersistenceMapper {

    // ── Teacher: domain → entity ──────────────────────────────────────────

    default TeacherEntity toEntity(Teacher domain) {
        if (domain == null) return null;

        TeacherEntity entity = new TeacherEntity();
        entity.setTeacherId(domain.getTeacherId().value());
        entity.setUserId(domain.getUserId().value());
        entity.setFirstName(domain.getFullName().firstName());
        entity.setLastName(domain.getFullName().lastName());
        entity.setDni(domain.getDni().value());
        entity.setCuil(domain.getCuil().value());
        entity.setEmail(domain.getEmail().value());
        entity.setBirthDate(domain.getBirthDate());
        entity.setBirthPlaceId(
                domain.getBirthPlaceId() != null ? domain.getBirthPlaceId().value() : null
        );
        entity.setGender(domain.getGender());
        entity.setNationality(
                domain.getNationality() != null ? domain.getNationality().value() : null
        );
        entity.setPhone(domain.getPhone().value());

        // Aplanar Address — columna place_id (igual que parents, distinto a students)
        if (domain.getAddress() != null) {
            Address address = domain.getAddress();
            entity.setAddressStreet(address.street());
            entity.setAddressNumber(address.number());
            entity.setAddressFloor(address.floor());
            entity.setAddressApartment(address.apartment());
            entity.setPlaceId(
                    address.placeId() != null ? address.placeId().value() : null
            );
            entity.setPostalCode(address.postalCode());
        }

        entity.setSpecialization(
                domain.getSpecialization() != null ? domain.getSpecialization().value() : null
        );
        entity.setTeachingLicense(domain.getTeachingLicense());
        entity.setHireDate(domain.getHireDate());
        entity.setEmploymentStatus(domain.getEmploymentStatus());
        entity.setEmploymentType(domain.getEmploymentType());
        entity.setActive(domain.isActive());
        entity.setActivationToken(domain.getActivationToken());
        entity.setActivationSentAt(domain.getActivationSentAt());
        entity.setActivatedAt(domain.getActivatedAt());
        entity.setCreatedBy(domain.getCreatedBy().value());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    // ── Teacher: entity → domain ──────────────────────────────────────────

    default Teacher toDomain(TeacherEntity entity) {
        if (entity == null) return null;

        // Reconstruir Address desde columnas aplanadas
        Address address = null;
        if (entity.getAddressStreet() != null && entity.getPlaceId() != null) {
            address = new Address(
                    entity.getAddressStreet(),
                    entity.getAddressNumber(),
                    entity.getAddressFloor(),
                    entity.getAddressApartment(),
                    PlaceId.of(entity.getPlaceId()),
                    entity.getPostalCode()
            );
        }

        return Teacher.builder()
                .teacherId(TeacherId.of(entity.getTeacherId()))
                .userId(UserId.from(entity.getUserId()))
                .fullName(FullName.of(entity.getFirstName(), entity.getLastName()))
                .dni(Dni.of(entity.getDni()))
                .cuil(Cuil.of(entity.getCuil()))
                .email(Email.of(entity.getEmail()))
                .birthDate(entity.getBirthDate())
                .birthPlaceId(
                        entity.getBirthPlaceId() != null
                                ? PlaceId.of(entity.getBirthPlaceId()) : null
                )
                .gender(entity.getGender())
                .nationality(
                        entity.getNationality() != null
                                ? Nationality.of(entity.getNationality()) : null
                )
                .phone(PhoneNumber.of(entity.getPhone()))
                .address(address)
                .specialization(
                        entity.getSpecialization() != null
                                ? TeacherSpecialization.of(entity.getSpecialization()) : null
                )
                .teachingLicense(entity.getTeachingLicense())
                .hireDate(entity.getHireDate())
                .employmentStatus(entity.getEmploymentStatus())
                .employmentType(entity.getEmploymentType())
                .active(entity.isActive())
                .activationToken(entity.getActivationToken())
                .activationSentAt(entity.getActivationSentAt())
                .activatedAt(entity.getActivatedAt())
                .createdBy(UserId.from(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}