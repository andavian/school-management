package org.school.management.auth.domain.valueobject;

import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Set;

@Value                                     // Inmutable
public class RoleName {
    private static final Set<String> VALID_ROLES = Set.of(
            "ADMIN", "TEACHER", "STUDENT", "PARENT", "STAFF"
    );

    String value;

    private RoleName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        String upperValue = value.trim().toUpperCase();

        if (!VALID_ROLES.contains(upperValue)) {
            throw new IllegalArgumentException("Invalid role name: " + value + ". Valid roles: " + VALID_ROLES);
        }

        this.value = upperValue;
    }

    public static RoleName of(String value) {
        return new RoleName(value);
    }

    // Factory methods para roles específicos
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

    public boolean isAdmin() {
        return "ADMIN".equals(value);
    }

    public boolean isTeacher() {
        return "TEACHER".equals(value);
    }

    public boolean isStudent() {
        return "STUDENT".equals(value);
    }

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(this.getValue());
    }
}
