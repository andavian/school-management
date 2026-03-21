package org.school.management.teachers.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.domain.event.AccountActivatedEvent;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

/**
 * Listener que reacciona al {@link AccountActivatedEvent} publicado por
 * {@code auth/ActivateAccountUseCase} cuando un usuario activa su cuenta.
 *
 * <p>Solo actúa cuando el rol es {@code ROLE_TEACHER} — otros roles son ignorados,
 * permitiendo que futuros listeners de otros BCs coexistan sin interferencia.</p>
 *
 * <p>{@code @TransactionalEventListener(phase = BEFORE_COMMIT)} garantiza que
 * la activación del {@code Teacher} ocurre dentro de la misma transacción que
 * activó el {@code User}. Si este listener falla, toda la transacción se revierte
 * — atomicidad garantizada sin acoplar {@code auth/} a {@code teachers/}.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TeacherAccountActivatedListener {

    private static final String TEACHER_ROLE = "ROLE_TEACHER";

    private final TeacherRepository teacherRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(AccountActivatedEvent event) {
        if (!TEACHER_ROLE.equals(event.roleName())) {
            return;
        }

        log.info("AccountActivatedEvent received for TEACHER — DNI: {}", event.dni());

        var teacher = teacherRepository.findByDni(Dni.of(event.dni()))
                .orElseThrow(() -> TeacherNotFoundException.byDni(event.dni()));

        teacher.activate(LocalDateTime.now());
        teacherRepository.save(teacher);

        log.info("Teacher activated successfully — DNI: {}, teacherId: {}",
                event.dni(), teacher.getTeacherId().asString());
    }
}