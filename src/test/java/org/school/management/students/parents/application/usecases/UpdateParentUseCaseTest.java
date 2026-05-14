package org.school.management.students.parents.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.application.dto.request.UpdateParentRequest;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.ParentNotFoundException;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.repository.ParentRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateParentUseCase")
class UpdateParentUseCaseTest {

    @Mock private ParentRepository parentRepository;
    @Mock private ParentApplicationMapper mapper;

    @InjectMocks private UpdateParentUseCase useCase;

    private static final UUID PARENT_ID = UUID.randomUUID();

    private Parent buildParent() {
        return Parent.builder()
                .parentId(org.school.management.students.parents.domain.valueobject.ParentId.of(PARENT_ID))
                .userId(org.school.management.auth.domain.valueobject.UserId.generate())
                .dni(Dni.of("12345678"))
                .cuil(Cuil.of("20123456786"))
                .fullName(FullName.of("Juan", "Pérez"))
                .email(Email.of("juan@test.com"))
                .phone(PhoneNumber.of("3511234567"))
                .phoneAlt(null)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1980, 5, 10))
                .nationality(Nationality.of("Argentina"))
                .occupation("Ingeniero")
                .workplace("Empresa S.A.")
                .workplacePhone(null)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .createdBy(org.school.management.auth.domain.valueobject.UserId.generate())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — actualiza datos personales")
    void execute_happyPath_updatesParent() {
        Parent parent = buildParent();
        UpdateParentRequest request = new UpdateParentRequest(
                "Carlos", "López", LocalDate.of(1980, 5, 10), "MALE", "Argentina",
                null, null, null, null, null, null, null, null, null, null, null, null);

        when(parentRepository.findByParentId(any())).thenReturn(Optional.of(parent));
        when(parentRepository.save(any(Parent.class))).thenReturn(parent);
        when(mapper.toParentResponse(any(Parent.class))).thenReturn(mock(ParentResponse.class));

        ParentResponse result = useCase.execute(PARENT_ID, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza ParentNotFoundException")
    void execute_notFound_throwsException() {
        UpdateParentRequest request = new UpdateParentRequest(
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);

        when(parentRepository.findByParentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(PARENT_ID, request))
                .isInstanceOf(ParentNotFoundException.class);
    }
}
