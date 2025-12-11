package org.school.management.academic.domain.valueobject;

import lombok.Value;

@Value
public class YearLevel {
    int value;

    public static YearLevel of(int value) {
        if (value < 1 || value > 7) {
            throw new IllegalArgumentException("Year level must be between 1 and 7");
        }
        return new YearLevel(value);
    }

    public String getDisplayName() {
        return value + "°";
    }

    public boolean requiresOrientation() {
        return value >= 4;
    }

     /**
     * Verifica si es ciclo básico (1°-3°)
     */
    public boolean isBasicCycle() {
        return value >= 1 && value <= 3;
    }

    /**
     * Verifica si es ciclo orientado (4°-7°)
     */
    public boolean isOrientedCycle() {
        return value >= 4 && value <= 7;
    }
}