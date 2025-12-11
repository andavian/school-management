package org.school.management.academic.domain.exception;

import org.school.management.academic.domain.valueobject.ids.GradeLevelId;

public class GradeLevelNotFoundException extends RuntimeException {

    // CONSTRUCTOR REQUERIDO: Permite pasar un mensaje de String personalizado.
    // Esto resuelve el error que tenías en el servicio.
    public GradeLevelNotFoundException(String message) {
        super(message);
    }

    // CONSTRUCTOR ORIGINAL 1: Sin argumentos, usa un mensaje por defecto.
    public GradeLevelNotFoundException() {
        super("Grade Level not found");
    }

    // CONSTRUCTOR ORIGINAL 2: Acepta el año como int para direccionar el error.
    public GradeLevelNotFoundException(GradeLevelId id) {
        super("Grade level not found: " + id);
    }
}


