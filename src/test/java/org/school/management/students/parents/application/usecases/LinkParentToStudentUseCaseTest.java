package org.school.management.students.parents.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.application.dto.request.LinkParentRequest;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.dto.response.StudentParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.DuplicatePrimaryContactException;
import org.school.management.students.parents.domain.exception.InvalidParentDataException;
import org.school.management.students.parents.domain.exception.ParentNotFoundException;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.model.StudentParent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.repository.StudentParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.parents.domain.valueobject.ParentRelationship;
import org.school.management.students.parents.domain.valueobject.StudentParentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("LinkParentToStudentUseCase")
class LinkParentToStudentUseCaseTest {

    @Mock private ParentRepository parentRepository;
    @Mock private StudentParentRepository studentParentRepository;
    @Mock private ParentApplicationMapper mapper;

    @InjectMocks private LinkParentToStudentUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID STUDENT_UUID = UUID.randomUUID();
    private static final UUID PARENT_UUID  = UUID.randomUUID();
    private static final String PARENT_DNI = "20345676";

    private Parent buildParent() {
        return Parent.builder()
                .parentId(ParentId.of(PARENT_UUID))
                .userId(UserId.of(UUID.randomUUID()))
                .dni(Dni.of(PARENT_DNI))
                .cuil(Cuil.of("23203456769"))
                .fullName(FullName.of("Ana", "García"))
                .email(Email.of("ana.garcia@gmail.com"))
                .phone(PhoneNumber.of("3514555666"))
                .createdBy(UserId.of(UUID.randomUUID()))
                .build();
    }

    private LinkParentRequest buildRequest(boolean isPrimaryContact) {
        return new LinkParentRequest(
                PARENT_DNI,
                "MOTHER",
                isPrimaryContact,
                true,
                true,
                null
        );
    }

    private StudentParent buildSavedStudentParent(boolean isPrimaryContact) {
        return StudentParent.builder()
                .studentParentId(StudentParentId.generate())
                .studentId(StudentPersonalDataId.from(STUDENT_UUID))
                .parentId(ParentId.of(PARENT_UUID))
                .relationship(ParentRelationship.MOTHER)
                .isPrimaryContact(isPrimaryContact)
                .isAuthorizedPickup(true)
                .isEmergencyContact(true)
                .build();
    }

    private StudentParentResponse buildStudentParentResponse(boolean isPrimaryContact) {
        ParentResponse parentResponse = new ParentResponse(
                PARENT_UUID, UUID.randomUUID(),
                PARENT_DNI, "23-20345676-9",
                "Ana", "García", "Ana García",
                LocalDate.of(1980, 5, 20),
                "FEMALE", "Argentina",
                "ana.garcia@gmail.com", "3514555666", null,
                null, null, null, null, null, null,
                null, null, null,
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        return new StudentParentResponse(
                UUID.randomUUID(),
                STUDENT_UUID,
                PARENT_UUID,
                ParentRelationship.MOTHER,
                "Madre",
                isPrimaryContact,
                true, true, null,
                parentResponse,
                LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz sin contacto principal — vincula correctamente")
    void execute_happyPath_linksParentToStudent() {
        Parent parent = buildParent();
        LinkParentRequest request = buildRequest(false);
        StudentParent saved = buildSavedStudentParent(false);
        StudentParentResponse response = buildStudentParentResponse(false);

        when(parentRepository.findByDni(Dni.of(PARENT_DNI)))
                .thenReturn(Optional.of(parent));
        when(studentParentRepository.existsByStudentIdAndParentId(
                StudentPersonalDataId.from(STUDENT_UUID), parent.getParentId()))
                .thenReturn(false);
        when(studentParentRepository.save(any(StudentParent.class)))
                .thenReturn(saved);
        when(mapper.toStudentParentResponse(saved, parent))
                .thenReturn(response);

        StudentParentResponse result = useCase.execute(STUDENT_UUID, request);

        assertThat(result).isNotNull();
        assertThat(result.parentId()).isEqualTo(PARENT_UUID);
        assertThat(result.relationship()).isEqualTo(ParentRelationship.MOTHER);
        verify(studentParentRepository).save(any(StudentParent.class));
    }

    @Test
    @DisplayName("execute — flujo feliz con isPrimaryContact — no verifica contacto existente")
    void execute_withPrimaryContact_whenNoPrimaryExists_thenLinks() {
        Parent parent = buildParent();
        LinkParentRequest request = buildRequest(true);
        StudentParent saved = buildSavedStudentParent(true);
        StudentParentResponse response = buildStudentParentResponse(true);

        when(parentRepository.findByDni(Dni.of(PARENT_DNI)))
                .thenReturn(Optional.of(parent));
        when(studentParentRepository.existsByStudentIdAndParentId(
                StudentPersonalDataId.from(STUDENT_UUID), parent.getParentId()))
                .thenReturn(false);
        when(studentParentRepository.existsPrimaryContactForStudent(
                StudentPersonalDataId.from(STUDENT_UUID)))
                .thenReturn(false);
        when(studentParentRepository.save(any(StudentParent.class)))
                .thenReturn(saved);
        when(mapper.toStudentParentResponse(saved, parent))
                .thenReturn(response);

        StudentParentResponse result = useCase.execute(STUDENT_UUID, request);

        assertThat(result).isNotNull();
        assertThat(result.primaryContact()).isTrue();
        verify(studentParentRepository).save(any(StudentParent.class));
    }

    @Test
    @DisplayName("execute — padre no existe — lanza ParentNotFoundException")
    void execute_whenParentNotFound_thenThrowParentNotFoundException() {
        LinkParentRequest request = buildRequest(false);

        when(parentRepository.findByDni(Dni.of(PARENT_DNI)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STUDENT_UUID, request))
                .isInstanceOf(ParentNotFoundException.class)
                .hasMessageContaining(PARENT_DNI);

        verifyNoInteractions(studentParentRepository);
        verify(mapper, never()).toStudentParentResponse(any(), any());
    }

    @Test
    @DisplayName("execute — vínculo ya existe — lanza InvalidParentDataException")
    void execute_whenLinkAlreadyExists_thenThrowInvalidParentDataException() {
        Parent parent = buildParent();
        LinkParentRequest request = buildRequest(false);

        when(parentRepository.findByDni(Dni.of(PARENT_DNI)))
                .thenReturn(Optional.of(parent));
        when(studentParentRepository.existsByStudentIdAndParentId(
                StudentPersonalDataId.from(STUDENT_UUID), parent.getParentId()))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(STUDENT_UUID, request))
                .isInstanceOf(InvalidParentDataException.class)
                .hasMessageContaining(PARENT_DNI);

        verify(studentParentRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — isPrimaryContact true pero ya existe uno — lanza DuplicatePrimaryContactException")
    void execute_whenPrimaryContactAlreadyExists_thenThrowDuplicatePrimaryContactException() {
        Parent parent = buildParent();
        LinkParentRequest request = buildRequest(true);

        when(parentRepository.findByDni(Dni.of(PARENT_DNI)))
                .thenReturn(Optional.of(parent));
        when(studentParentRepository.existsByStudentIdAndParentId(
                StudentPersonalDataId.from(STUDENT_UUID), parent.getParentId()))
                .thenReturn(false);
        when(studentParentRepository.existsPrimaryContactForStudent(
                StudentPersonalDataId.from(STUDENT_UUID)))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(STUDENT_UUID, request))
                .isInstanceOf(DuplicatePrimaryContactException.class)
                .hasMessageContaining(STUDENT_UUID.toString());

        verify(studentParentRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — isPrimaryContact false — no verifica contacto principal existente")
    void execute_whenNotPrimaryContact_thenDoNotCheckExistingPrimary() {
        Parent parent = buildParent();
        LinkParentRequest request = buildRequest(false);
        StudentParent saved = buildSavedStudentParent(false);

        when(parentRepository.findByDni(Dni.of(PARENT_DNI)))
                .thenReturn(Optional.of(parent));
        when(studentParentRepository.existsByStudentIdAndParentId(
                StudentPersonalDataId.from(STUDENT_UUID), parent.getParentId()))
                .thenReturn(false);
        when(studentParentRepository.save(any(StudentParent.class)))
                .thenReturn(saved);
        when(mapper.toStudentParentResponse(any(), any()))
                .thenReturn(buildStudentParentResponse(false));

        useCase.execute(STUDENT_UUID, request);

        // No debe verificar contacto principal si isPrimaryContact = false
        verify(studentParentRepository, never())
                .existsPrimaryContactForStudent(any());
    }
}
