package org.school.management.academic.domain.exception;

public class NoActiveRegistryException extends RuntimeException {

    // CONSTRUCTOR REQUERIDO: Permite pasar un mensaje de String personalizado.
    // Esto resuelve el error que tenías en el servicio.
    public NoActiveRegistryException(String message) {
        super(message);
    }

    // CONSTRUCTOR ORIGINAL 1: Sin argumentos, usa un mensaje por defecto.
    public NoActiveRegistryException() {
        super("No active qualification registry found for current academic year");
    }

    // CONSTRUCTOR ORIGINAL 2: Acepta el año como int para direccionar el error.
    public NoActiveRegistryException(int year) {
        super("No active qualification registry found for year: " + year);
    }
}