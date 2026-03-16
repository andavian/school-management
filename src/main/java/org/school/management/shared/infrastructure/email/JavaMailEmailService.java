package org.school.management.shared.infrastructure.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.domain.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaMailEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.school.name}")
    private String schoolName;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    // ── Puerto genérico ───────────────────────────────────────────────────

    @Async
    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to: {} — reason: {}", to, e.getMessage());
            // No relanzamos — el email no debe romper el flujo transaccional principal
        }
    }

    // ── Invitación teacher ────────────────────────────────────────────────

    @Async
    @Override
    public void sendTeacherInvitation(
            String to,
            String firstName,
            String lastName,
            String dni,
            String temporaryPassword,
            String activationLink) {

        String subject = schoolName + " — Invitación para activar tu cuenta docente";

        String body = buildTeacherInvitationBody(
                firstName, lastName, dni, temporaryPassword, activationLink
        );

        sendEmail(to, subject, body);
    }

    // ── Credenciales parent ───────────────────────────────────────────────

    @Async
    @Override
    public void sendParentCredentials(
            String to,
            String firstName,
            String lastName,
            String dni,
            String temporaryPassword) {

        String subject = schoolName + " — Tus credenciales de acceso al sistema escolar";

        String body = buildParentCredentialsBody(
                firstName, lastName, dni, temporaryPassword
        );

        sendEmail(to, subject, body);
    }

    // ── Builders de cuerpo ────────────────────────────────────────────────

    private String buildTeacherInvitationBody(
            String firstName,
            String lastName,
            String dni,
            String temporaryPassword,
            String activationLink) {

        String activationSection = (activationLink != null && !activationLink.isBlank())
                ? """
              Para activar su cuenta, ingrese al siguiente enlace:
              
                %s
              
              Este enlace es válido por 48 horas.
              """.formatted(activationLink)
                : """
              Para activar su cuenta, comuníquese con la administración \
              del establecimiento quien le indicará los pasos a seguir.
              """;

        return """
            Estimado/a %s %s,
            
            Le informamos que se ha creado una cuenta docente en el sistema \
            de gestión escolar del %s.
            
            Sus credenciales de acceso son:
            
              Usuario (DNI): %s
              Contraseña temporal: %s
            
            %s
            Por seguridad, le recomendamos cambiar su contraseña al ingresar \
            por primera vez.
            
            Saludos,
            Equipo de administración
            %s
            
            ---
            Este es un mensaje automático. Por favor no responda a este correo.
            """.formatted(firstName, lastName, schoolName, dni, temporaryPassword,
                activationSection, schoolName);
    }

    private String buildParentCredentialsBody(
            String firstName,
            String lastName,
            String dni,
            String temporaryPassword) {

        return """
                Estimado/a %s %s,
                
                Se ha creado una cuenta en el sistema de gestión escolar del %s
                para que pueda acceder al seguimiento académico de su hijo/a.
                
                Sus credenciales de acceso son:
                
                  Usuario (DNI): %s
                  Contraseña temporal: %s
                
                Para ingresar al sistema acceda a:
                
                  %s
                
                Por seguridad, le recomendamos cambiar su contraseña al ingresar por primera vez.
                
                Ante cualquier inconveniente comuníquese con la administración del establecimiento.
                
                Saludos,
                Equipo de administración
                %s
                
                ---
                Este es un mensaje automático. Por favor no responda a este correo.
                """.formatted(firstName, lastName, schoolName, dni, temporaryPassword,
                frontendUrl, schoolName);
    }
}