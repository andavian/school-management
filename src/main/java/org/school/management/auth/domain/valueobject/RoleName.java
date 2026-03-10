package org.school.management.auth.domain.valueobject;

public record RoleName(String name) implements GrantedAuthority {

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
    public static RoleName of(String value) {
        return new RoleName(value);
    }

    public static RoleName admin() {
        return new RoleName("ADMIN");
    }

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

    // Métodos de negocio
    public boolean isAdmin() {
        return "ADMIN".equals(name);
    }

    public boolean isTeacher() {
        return "TEACHER".equals(name);
    }

    public boolean isStudent() {
        return "STUDENT".equals(name);
    }

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + name);
    }

    // GrantedAuthority — Spring Security
    @Override
    public String getAuthority() {
        return "ROLE_" + name;
    }

    // toString explícito — record generaría RoleName[name=ADMIN]
    // pero el resto del sistema espera solo el nombre
    @Override
    public String toString() {
        return name;
    }
}
