package org.school.management.students.parents.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.application.dto.request.UpdateParentRequest;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.ParentAlreadyExistsException;
import org.school.management.students.parents.domain.exception.ParentNotFoundException;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Actualiza datos del padre/tutor — semántica PATCH.
 * DNI no es modificable — es el identificador global.
 * Cada sección (personal, contacto, laboral) se actualiza
 * solo si viene al menos un campo de esa sección.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateParentUseCase {

    private final ParentRepository parentRepository;
    private final ParentApplicationMapper mapper;

    public ParentResponse execute(UUID parentId, UpdateParentRequest request) {
        log.debug("Updating parent — parentId: {}", parentId);

        Parent parent = parentRepository
                .findByParentId(ParentId.of(parentId))
                .orElseThrow(() -> ParentNotFoundException.byId(parentId));

        // ── Validar unicidad de email si cambia ───────────────────────────
        if (request.email() != null) {
            Email newEmail = Email.of(request.email());
            boolean emailChanged = !newEmail.value()
                    .equalsIgnoreCase(parent.getEmail().value());

            if (emailChanged && parentRepository.existsByEmail(newEmail)) {
                throw ParentAlreadyExistsException.withEmail(request.email());
            }
        }

        // ── Actualizar datos personales si vienen ─────────────────────────
        if (hasPersonalDataChanges(request)) {
            parent.updatePersonalInfo(
                    buildFullName(request, parent),
                    request.birthDate() != null
                            ? request.birthDate() : parent.getBirthDate(),
                    request.gender() != null
                            ? Gender.valueOf(request.gender()) : parent.getGender(),
                    request.nationality() != null
                            ? Nationality.of(request.nationality()) : parent.getNationality()
            );
        }

        // ── Actualizar contacto si viene ──────────────────────────────────
        if (hasContactChanges(request)) {
            parent.updateContactInfo(
                    request.email() != null
                            ? Email.of(request.email()) : parent.getEmail(),
                    request.phone() != null
                            ? PhoneNumber.of(request.phone()) : parent.getPhone(),
                    request.phoneAlt() != null
                            ? PhoneNumber.of(request.phoneAlt()) : parent.getPhoneAlt(),
                    buildAddress(request, parent)
            );
        }

        // ── Actualizar info laboral si viene ──────────────────────────────
        if (hasWorkChanges(request)) {
            parent.updateWorkInfo(
                    request.occupation() != null
                            ? request.occupation() : parent.getOccupation(),
                    request.workplace() != null
                            ? request.workplace() : parent.getWorkplace(),
                    request.workplacePhone() != null
                            ? PhoneNumber.of(request.workplacePhone()) : parent.getWorkplacePhone()
            );
        }

        Parent saved = parentRepository.save(parent);
        log.info("Parent updated successfully — parentId: {}", parentId);

        return mapper.toParentResponse(saved);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private boolean hasPersonalDataChanges(UpdateParentRequest request) {
        return request.firstName() != null
                || request.lastName() != null
                || request.birthDate() != null
                || request.gender() != null
                || request.nationality() != null;
    }

    private boolean hasContactChanges(UpdateParentRequest request) {
        return request.email() != null
                || request.phone() != null
                || request.phoneAlt() != null
                || request.addressStreet() != null
                || request.residencePlaceId() != null;
    }

    private boolean hasWorkChanges(UpdateParentRequest request) {
        return request.occupation() != null
                || request.workplace() != null
                || request.workplacePhone() != null;
    }

    private FullName buildFullName(UpdateParentRequest request, Parent parent) {
        String firstName = request.firstName() != null
                ? request.firstName() : parent.getFullName().firstName();
        String lastName = request.lastName() != null
                ? request.lastName() : parent.getFullName().lastName();
        return FullName.of(firstName, lastName);
    }

    private Address buildAddress(UpdateParentRequest request, Parent parent) {
        if (request.addressStreet() == null && request.residencePlaceId() == null) {
            return parent.getAddress();
        }

        Address current = parent.getAddress();

        return new Address(
                request.addressStreet() != null
                        ? request.addressStreet()
                        : (current != null ? current.street() : null),
                request.addressNumber() != null
                        ? request.addressNumber()
                        : (current != null ? current.number() : null),
                request.addressFloor() != null
                        ? request.addressFloor()
                        : (current != null ? current.floor() : null),
                request.addressApartment() != null
                        ? request.addressApartment()
                        : (current != null ? current.apartment() : null),
                request.residencePlaceId() != null
                        ? PlaceId.of(request.residencePlaceId())
                        : (current != null ? current.placeId() : null),
                request.postalCode() != null
                        ? request.postalCode()
                        : (current != null ? current.postalCode() : null)
        );
    }
}