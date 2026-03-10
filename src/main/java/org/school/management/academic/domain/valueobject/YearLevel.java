package org.school.management.academic.domain.valueobject;

public record YearLevel(int value) {

    public YearLevel {
        if (value < 1 || value > 7) {
            throw new IllegalArgumentException("Year level must be between 1 and 7");
        }
    }

    public static YearLevel of(int value) {
        return new YearLevel(value);
    }

    public String getDisplayName() {
        return value + "°";
    }

    public boolean requiresOrientation() {
        return value >= 4;
    }

    public boolean isBasicCycle() {
        return value >= 1 && value <= 3;
    }

    public boolean isOrientedCycle() {
        return value >= 4 && value <= 7;
    }
}
