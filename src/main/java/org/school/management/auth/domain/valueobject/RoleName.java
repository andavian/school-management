package org.school.management.auth.domain.valueobject;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Set;

@Value
public class RoleName implements GrantedAuthority {


    private static final Set<String> VALID_ROLES = Set.of(
            "ADMIN", "TEACHER", "STUDENT", "PARENT", "STAFF"
    );

    String name;

    private RoleName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        String upperValue = name.trim().toUpperCase();

        if (!VALID_ROLES.contains(upperValue)) {
            throw new IllegalArgumentException("Invalid role name: " + name + ". Valid roles: " + VALID_ROLES);
        }

        this.name = upperValue;
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
        return "ADMIN".equals(name);
    }

    public boolean isTeacher() {
        return "TEACHER".equals(name);
    }

    public boolean isStudent() {
        return "STUDENT".equals(name);
    }

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(this.getName());
    }

           @Override
        public String getAuthority() {
            return "ROLE_" + name;  // convención de Spring Security
        }

        @Override
        public String toString() {
            return name;
        }
    }
