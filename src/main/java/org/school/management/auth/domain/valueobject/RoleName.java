package org.school.management.auth.domain.valueobject;

import java.util.Set;

public record RoleName(String name) {

    private static final Set<String> VALID_ROLES = Set.of(
            "SUPER_ADMIN", "ADMIN", "PRINCIPAL", "TEACHER",
            "STUDENT", "PARENT", "STAFF", "PRECEPTOR"
    );

    public RoleName {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        String cleanName = name.trim().toUpperCase();
        if (cleanName.startsWith("ROLE_")) {
            cleanName = cleanName.substring(5);
        }
        if (!VALID_ROLES.contains(cleanName)) {
            throw new IllegalArgumentException(
                    "Invalid role name: " + name + ". Valid roles: " + VALID_ROLES);
        }

        name = cleanName;
    }

    // Factory methods
    public static RoleName of(String value) {
        return new RoleName(value);
    }


    public static RoleName superAdmin() { return new RoleName("SUPER_ADMIN"); }

    public static RoleName admin() {
        return new RoleName("ADMIN");
    }


    public static RoleName principal() { return new RoleName("PRINCIPAL");}

    public static RoleName teacher() {
        return new RoleName("TEACHER");
    }

    public static RoleName student() {
        return new RoleName("STUDENT");
    }

    public static RoleName parent() {
        return new RoleName("PARENT");
    }

    public static RoleName staff() {
        return new RoleName("STAFF");
    }

    public static RoleName preceptor() {
        return new RoleName("PRECEPTOR");
    }

    // Métodos de negocio
    public boolean isAdmin() { return "ADMIN".equals(name); }
    public boolean isTeacher() { return "TEACHER".equals(name); }
    public boolean isStudent() { return "STUDENT".equals(name); }

    public String toDbName() {
        return name; // Devuelve "ADMIN", "TEACHER", etc.
    }

    // Este es el que usará Spring Security en memoria
    public String toSpringRole() {
        return "ROLE_" + name; // Devuelve "ROLE_ADMIN"
    }

    @Override
    public String toString() {
        return name;
    }
}