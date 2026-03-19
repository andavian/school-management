package org.school.management.auth.domain.valueobject;

import java.util.Set;

public record RoleName(String name) {

    private static final Set<String> VALID_ROLES = Set.of(
            "ADMIN", "TEACHER", "STUDENT", "PARENT", "STAFF"
    );

    public RoleName {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        name = name.trim().toUpperCase();
        if (!VALID_ROLES.contains(name)) {
            throw new IllegalArgumentException(
                    "Invalid role name: " + name + ". Valid roles: " + VALID_ROLES);
        }
    }

    // Factory methods
    public static RoleName of(String value) { return new RoleName(value); }
    public static RoleName admin() { return new RoleName("ADMIN"); }
    // ... el resto de tus factories ...

    // Métodos de negocio
    public boolean isAdmin() { return "ADMIN".equals(name); }
    public boolean isTeacher() { return "TEACHER".equals(name); }
    public boolean isStudent() { return "STUDENT".equals(name); }

    // Generamos el string con el prefijo esperado por Spring,
    // pero sin depender de sus clases.
    public String toRoleString() {
        return "ROLE_" + name;
    }

    @Override
    public String toString() {
        return name;
    }
}