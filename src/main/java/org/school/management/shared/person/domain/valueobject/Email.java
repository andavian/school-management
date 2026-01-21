// src/main/java/org/school/management/shared/person/domain/valueobject/Email.java
package org.school.management.shared.person.domain.valueobject;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Value Object inmutable para email – estándar nacional argentino 2025
 * Cumple RFC 5321/5322 + mejores prácticas educativas
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final int MAX_LENGTH = 254;

    // Dominios temporales comunes en Argentina (actualizado 2025)
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "10minutemail.com", "tempmail.org", "guerrillamail.com",
            "yopmail.com", "mailinator.com", "disposablemail.com",
            "throwawaymail.com", "temp-mail.org", "sharklasers.com"
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }

        String trimmed = value.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "El email no puede exceder los " + MAX_LENGTH + " caracteres"
            );
        }

        String lowerCase = trimmed.toLowerCase();

        // Permite localhost en desarrollo
        if (!lowerCase.contains("@localhost") && !EMAIL_PATTERN.matcher(lowerCase).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + trimmed);
        }

        // Bloqueo de emails temporales (salvo en dev)
        String domain = lowerCase.substring(lowerCase.indexOf('@') + 1);
        if (DISPOSABLE_DOMAINS.contains(domain) && !"dev".equals(System.getProperty("spring.profiles.active"))) {
            throw new IllegalArgumentException("No se permiten emails temporales o desechables");
        }

        value = lowerCase;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    /**
     * Detecta si es un email institucional del colegio
     */
    public boolean isFromSchool() {
        return getDomain().endsWith("ipet132.edu.ar");
    }

    /**
     * Detecta emails personales (Gmail, Hotmail, Yahoo, Outlook, iCloud)
     */
    public boolean isPersonal() {
        String domain = getDomain();
        return domain.matches(".*(gmail|hotmail|yahoo|outlook|live|icloud|protonmail)\\.com.*");
    }

    /**
     * Permite configurar dominios institucionales externos (ej: padres con @educ.ar)
     */
    public boolean isInstitutional(String... allowedDomains) {
        String domain = getDomain();
        for (String allowed : allowedDomains) {
            if (domain.equalsIgnoreCase(allowed) || domain.endsWith("." + allowed)) {
                return true;
            }
        }
        return isFromSchool();
    }



    @Override
    public String toString() {
        return value;
    }
}