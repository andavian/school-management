package org.school.management.academic.domain.valueobject;

public record WeeklyHours(int value) {

    public WeeklyHours {
        if (value <= 0) {
            throw new IllegalArgumentException("Weekly hours must be positive");
        }
        if (value > 20) {
            throw new IllegalArgumentException("Weekly hours cannot exceed 20");
        }
    }

    public static WeeklyHours of(int value) {
        return new WeeklyHours(value);
    }
}
