package org.school.management.students.enrollment.domain.valueobject;



public enum EnrollmentStatus {
    ACTIVE("Activo"),
    INACTIVE("Pasivo"),
    SUSPENDED("Suspendido"),
    GRADUATED("Egresado"),
    WITHDRAWN("Retirado"),
    TRANSFERRED("Trasladado");

    private final String displayName;

    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }
}