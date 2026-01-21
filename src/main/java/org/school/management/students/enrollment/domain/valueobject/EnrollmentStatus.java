package org.school.management.students.enrollment.domain.valueobject;

public enum EnrollmentStatus {
    ACTIVE("Activo", true),
    INACTIVE("Pasivo", false),
    SUSPENDED("Suspendido", false),
    COMPLETED("Completado", false),
    GRADUATED("Egresado", false),
    WITHDRAWN("Retirado", false),
    TRANSFERRED("Trasladado", false);

    private final boolean canReceiveGrades;

    EnrollmentStatus(String displayName, boolean canReceiveGrades) {
        this.canReceiveGrades = canReceiveGrades;
    }

    public boolean canReceiveGrades() {
        return canReceiveGrades;
    }
}