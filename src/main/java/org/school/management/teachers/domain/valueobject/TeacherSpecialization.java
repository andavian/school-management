package org.school.management.teachers.domain.valueobject;

public record TeacherSpecialization(String value) {

    public TeacherSpecialization {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("TeacherSpecialization cannot be blank");
        }
        if (value != null && value.length() > 200) {
            throw new IllegalArgumentException("TeacherSpecialization cannot exceed 200 characters");
        }
        value = value != null ? value.trim() : null;
    }

    public static TeacherSpecialization of(String value) {
        return new TeacherSpecialization(value);
    }

    public static TeacherSpecialization empty() {
        return new TeacherSpecialization(null);
    }
}