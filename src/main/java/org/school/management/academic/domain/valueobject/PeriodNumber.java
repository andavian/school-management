package org.school.management.academic.domain.valueobject;

import lombok.Value;


@Value
public class PeriodNumber {
    private static final int MIN_PERIOD = 1;
    private static final int MAX_PERIOD = 4;

    int value;


    private PeriodNumber(int value) {
        this.value = value;
    }

    public static PeriodNumber of(int value) {
        if (value < MIN_PERIOD || value > MAX_PERIOD) {
            // Usamos IllegalArgumentException por simplicidad, pero se recomienda una excepción de dominio.
            String message = String.format("Period number must be between %d and %d, but received %d.",
                    MIN_PERIOD, MAX_PERIOD, value);
            throw new IllegalArgumentException(message);
        }
        return new PeriodNumber(value);
    }

    public PeriodNumber next() {
        if (value >= MAX_PERIOD) {
            throw new IllegalStateException("Cannot advance period: already at maximum period number.");
        }
        return new PeriodNumber(value + 1);
    }
}