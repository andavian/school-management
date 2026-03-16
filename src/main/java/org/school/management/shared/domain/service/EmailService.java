package org.school.management.shared.domain.service;

/**
 * Puerto del dominio para envío de emails.
 * Sin dependencias de Spring ni infraestructura — dominio puro.
 * Implementado por JavaMailEmailService en shared/infrastructure/email/.
 */
public interface EmailService {

    /**
     * Envía un email de texto plano.
     *
     * @param to      destinatario
     * @param subject asunto
     * @param body    cuerpo en texto plano
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Envía invitación de activación a un profesor.
     */
    void sendTeacherInvitation(
            String to,
            String firstName,
            String lastName,
            String dni,
            String temporaryPassword,
            String activationLink
    );

    /**
     * Envía credenciales iniciales a un padre/tutor.
     */
    void sendParentCredentials(
            String to,
            String firstName,
            String lastName,
            String dni,
            String temporaryPassword
    );
}