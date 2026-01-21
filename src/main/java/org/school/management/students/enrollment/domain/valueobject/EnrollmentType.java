package org.school.management.students.enrollment.domain.valueobject;

public enum EnrollmentType {
    NEW("Nuevo"),
    REPEATING("Repitente"),
    RETURNING("Reingreso"),
    TRANSFER("Transferido");

    private final String displayName;

    EnrollmentType(String displayName) {
        this.displayName = displayName;
    }
}