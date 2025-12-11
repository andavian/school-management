package org.school.management.academic.domain.valueobject;

import lombok.Value;

@Value
public class Year {
    int value;

    public static final int MIN_YEAR = 2000;
    public static final int MAX_YEAR = 2100;

    public static Year of(int value) {
        if (value < MIN_YEAR || value > MAX_YEAR) {
            // Se asume la existencia de una excepción de dominio (ej. InvalidYearException)
            throw new IllegalArgumentException(
                    String.format("Academic year must be between %d and %d", MIN_YEAR, MAX_YEAR)
            );
        }
        return new Year(value);
    }

    public static Year current() {
        // Mejor práctica: Llama a of() para garantizar que el año actual también sea validado
        return of(java.time.Year.now().getValue());
    }

    public Year next() {
        // Llama a of() para validar que el año siguiente no exceda el MAX_YEAR
        return of(value + 1);
    }

    public Year previous() {
        // Llama a of() para validar que el año anterior no caiga bajo el MIN_YEAR
        return of(value - 1);
    }
}