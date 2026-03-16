package org.school.management.teachers.domain.valueobject;

public enum EmploymentStatus {
    ACTIVE,
    INACTIVE,
    RETIRED;

    public boolean isTerminal() {
        return this == RETIRED;
    }
}