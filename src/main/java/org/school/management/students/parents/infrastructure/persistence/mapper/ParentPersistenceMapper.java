package org.school.management.students.parents.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.model.StudentParent;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.parents.domain.valueobject.StudentParentId;
import org.school.management.students.parents.infrastructure.persistence.entity.ParentEntity;
import org.school.management.students.parents.infrastructure.persistence.entity.StudentParentEntity;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

/**
 * PersistenceMapper para Parent y StudentParent.
 * Usa métodos default por la complejidad de VOs compuestos (FullName, Address).
 * Patrón consistente con StudentPersonalDataPersistenceMapper.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ParentPersistenceMapper {

    // ── Parent: domain → entity ───────────────────────────────────────────

    default ParentEntity toEntity(Parent domain) {
        if (domain == null) return null;

        ParentEntity entity = new ParentEntity();
        entity.setParentId(domain.getParentId().value());
        entity.setUserId(domain.getUserId().value());
        entity.setDni(domain.getDni().value());
        entity.setFirstName(domain.getFullName().firstName());
        entity.setLastName(domain.getFullName().lastName());
        entity.setBirthDate(domain.getBirthDate());
        entity.setGender(domain.getGender());
        entity.setNationality(
                domain.getNationality() != null ? domain.getNationality().value() : null
        );
        entity.setEmail(domain.getEmail().value());
        entity.setPhone(domain.getPhone().value());
        entity.setPhoneAlt(
                domain.getPhoneAlt() != null ? domain.getPhoneAlt().value() : null
        );

        // Aplanar Address
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

        entity.setOccupation(domain.getOccupation());
        entity.setWorkplace(domain.getWorkplace());
        entity.setWorkplacePhone(
                domain.getWorkplacePhone() != null
                        ? domain.getWorkplacePhone().value() : null
        );
        entity.setActive(domain.isActive());
        entity.setCreatedBy(domain.getCreatedBy().value());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    // ── Parent: entity → domain ───────────────────────────────────────────

    default Parent toDomain(ParentEntity entity) {
        if (entity == null) return null;

        // Reconstruir Address
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

        return Parent.builder()
                .parentId(ParentId.of(entity.getParentId()))
                .userId(UserId.from(entity.getUserId()))
                .dni(Dni.of(entity.getDni()))
                .fullName(FullName.of(entity.getFirstName(), entity.getLastName()))
                .birthDate(entity.getBirthDate())
                .gender(entity.getGender())
                .nationality(entity.getNationality() != null
                        ? Nationality.of(entity.getNationality()) : null)
                .email(Email.of(entity.getEmail()))
                .phone(PhoneNumber.of(entity.getPhone()))
                .phoneAlt(entity.getPhoneAlt() != null
                        ? PhoneNumber.of(entity.getPhoneAlt()) : null)
                .address(address)
                .occupation(entity.getOccupation())
                .workplace(entity.getWorkplace())
                .workplacePhone(entity.getWorkplacePhone() != null
                        ? PhoneNumber.of(entity.getWorkplacePhone()) : null)
                .isActive(entity.isActive())
                .createdBy(UserId.from(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ── StudentParent: domain → entity ────────────────────────────────────

    default StudentParentEntity toStudentParentEntity(StudentParent domain) {
        if (domain == null) return null;

        StudentParentEntity entity = new StudentParentEntity();
        entity.setStudentParentId(domain.getStudentParentId().value());
        entity.setStudentId(domain.getStudentId().value());
        entity.setParentId(domain.getParentId().value());
        entity.setRelationship(domain.getRelationship());
        entity.setPrimaryContact(domain.isPrimaryContact());
        entity.setAuthorizedPickup(domain.isAuthorizedPickup());
        entity.setEmergencyContact(domain.isEmergencyContact());
        entity.setNotes(domain.getNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    // ── StudentParent: entity → domain ────────────────────────────────────

    default StudentParent toStudentParentDomain(StudentParentEntity entity) {
        if (entity == null) return null;

        return StudentParent.builder()
                .studentParentId(StudentParentId.of(entity.getStudentParentId()))
                .studentId(StudentPersonalDataId.of(entity.getStudentId()))
                .parentId(ParentId.of(entity.getParentId()))
                .relationship(entity.getRelationship())
                .isPrimaryContact(entity.isPrimaryContact())
                .isAuthorizedPickup(entity.isAuthorizedPickup())
                .isEmergencyContact(entity.isEmergencyContact())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}