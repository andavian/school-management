package org.school.management.teachers.infrastructure.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.shared.domain.event.AccountActivatedEvent;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TeacherAccountActivatedListener")
class TeacherAccountActivatedListenerTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private Teacher           teacher;

    @InjectMocks private TeacherAccountActivatedListener listener;

    // ─── helpers ────────────────────────────────────────────────────────────

    private static final String DNI = "20345676";

    private AccountActivatedEvent eventWithRole(String roleName) {
        return AccountActivatedEvent.of(UUID.randomUUID(), DNI, roleName);
    }

    // ─── tests ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("evento ROLE_TEACHER → activa Teacher y guarda")
    void teacherRole_activatesAndSaves() {
        TeacherId mockTeacherId = TeacherId.generate(); // o usa un valor fijo para pruebas
        when(teacher.getTeacherId()).thenReturn(mockTeacherId);

        when(teacherRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.of(teacher));

        listener.handle(eventWithRole("ROLE_TEACHER"));

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(teacher).activate(captor.capture());
        assertThat(captor.getValue()).isNotNull().isBefore(LocalDateTime.now().plusSeconds(1));

        verify(teacherRepository).save(teacher);
    }

    @Test
    @DisplayName("evento con rol distinto → retorna sin tocar repositorio")
    void otherRole_ignoresEvent() {
        listener.handle(eventWithRole("ROLE_STUDENT"));

        verifyNoInteractions(teacherRepository);
    }

    @Test
    @DisplayName("evento ROLE_TEACHER pero teacher no encontrado → lanza TeacherNotFoundException")
    void teacherRole_teacherNotFound_throwsException() {
        when(teacherRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listener.handle(eventWithRole("ROLE_TEACHER")))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(teacherRepository, never()).save(any());
    }
}