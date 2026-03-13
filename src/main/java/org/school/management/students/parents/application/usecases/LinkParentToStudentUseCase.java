package org.school.management.students.parents.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.parents.application.dto.request.LinkParentRequest;
import org.school.management.students.parents.application.dto.response.StudentParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.DuplicatePrimaryContactException;
import org.school.management.students.parents.domain.exception.InvalidParentDataException;
import org.school.management.students.parents.domain.exception.ParentNotFoundException;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.model.StudentParent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.repository.StudentParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentRelationship;
import org.school.management.students.parents.domain.valueobject.StudentParentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Vincula un padre/tutor existente a un estudiante.
 *
 * Flujo:
 * 1. Buscar el padre por DNI — debe existir previamente
 * 2. Validar que el vínculo no exista ya
 * 3. Validar unicidad de contacto principal si isPrimaryContact = true
 * 4. Crear StudentParent
 *
 * Para crear un padre nuevo y vincularlo en un solo paso,
 * usar CreateStudentUseCase (flujo de inscripción) o
 * CreateParentUseCase + LinkParentToStudentUseCase por separado.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LinkParentToStudentUseCase {

    private final ParentRepository parentRepository;
    private final StudentParentRepository studentParentRepository;
    private final ParentApplicationMapper mapper;

    public StudentParentResponse execute(UUID studentId, LinkParentRequest request) {
        log.debug("Linking parent to student — studentId: {}, parentDni: {}",
                studentId, request.parentDni());

        StudentPersonalDataId studentPersonalDataId =
                StudentPersonalDataId.from(studentId);

        // ── Paso 1: Buscar padre por DNI ──────────────────────────────────
        Dni dni = Dni.of(request.parentDni());
        Parent parent = parentRepository.findByDni(dni)
                .orElseThrow(() -> ParentNotFoundException.byDni(request.parentDni()));

        // ── Paso 2: Validar que el vínculo no exista ──────────────────────
        if (studentParentRepository.existsByStudentIdAndParentId(
                studentPersonalDataId, parent.getParentId())) {
            throw new InvalidParentDataException(
                    "Parent with DNI " + request.parentDni()
                            + " is already linked to student: " + studentId
            );
        }

        // ── Paso 3: Validar unicidad de contacto principal ─────────────────
        boolean isPrimary = Boolean.TRUE.equals(request.isPrimaryContact());
        if (isPrimary && studentParentRepository
                .existsPrimaryContactForStudent(studentPersonalDataId)) {
            throw DuplicatePrimaryContactException.forStudent(studentId);
        }

        // ── Paso 4: Crear StudentParent ───────────────────────────────────
        StudentParent studentParent = StudentParent.create(
                StudentParent.builder()
                        .studentParentId(StudentParentId.generate())
                        .studentId(studentPersonalDataId)
                        .parentId(parent.getParentId())
                        .relationship(ParentRelationship.valueOf(request.relationship()))
                        .isPrimaryContact(isPrimary)
                        .isAuthorizedPickup(
                                Boolean.TRUE.equals(request.isAuthorizedPickup())
                        )
                        .isEmergencyContact(
                                !Boolean.FALSE.equals(request.isEmergencyContact())
                        )
                        .notes(request.notes())
        );

        StudentParent saved = studentParentRepository.save(studentParent);

        log.info("Parent linked to student — studentId: {}, parentId: {}, relationship: {}",
                studentId, parent.getParentId().value(), request.relationship());

        return mapper.toStudentParentResponse(saved, parent);
    }
}
