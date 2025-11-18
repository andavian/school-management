package org.school.management.students.domain.valueobject;

import jakarta.validation.constraints.NotNull;


/**
 * @param value 1-7
 */
public record GradeLevel(int value) {
    public static GradeLevel of(int value) {
        if (value < 1 || value > 7) {
            throw new IllegalArgumentException("Invalid grade level");
        }
        return new GradeLevel(value);
    }

    @Override
    public @NotNull String toString() {
        return String.valueOf(value);
    }
}