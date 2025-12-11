package org.school.management.academic.domain.valueobject;

import lombok.Value;

@Value
public class WeeklyHours {
    int value;

    public static WeeklyHours of(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Weekly hours must be positive");
        }
        if (value > 20) {
            throw new IllegalArgumentException("Weekly hours cannot exceed 20");
        }
        return new WeeklyHours(value);
    }
}