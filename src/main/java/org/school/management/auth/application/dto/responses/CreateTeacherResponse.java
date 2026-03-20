package org.school.management.auth.application.dto.responses;

/**
 * Respuesta del use case de creación de usuario Teacher en auth/.
 *
 * confirmationToken: JWT de tipo CONFIRMATION (48h) que se incluye en el
 * link de activación enviado por email. teachers/CreateTeacherUseCase lo
 * propaga a EmailService.sendTeacherInvitation().
 *
 * NOTA: temporaryPassword solo se usa en dev/testing — en producción
 * el profesor recibe la contraseña temporal únicamente por email.
 */
public record CreateTeacherResponse(
        String userId,
        String dni,
        String temporaryPassword,
        boolean invitationSent,
        String confirmationToken      // ← nuevo campo
) {}