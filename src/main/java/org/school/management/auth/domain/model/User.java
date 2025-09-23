package org.school.management.auth.domain.model;

import lombok.*;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotActiveException;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.domain.valueobjects.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter                                    // Solo getters automáticos
@ToString(exclude = "password")            // toString sin password por seguridad
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Solo campos específicos
@NoArgsConstructor(access = AccessLevel.PRIVATE)   // Constructor privado para JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Constructor privado para factory methods
public class User implements UserDetails {

    @EqualsAndHashCode.Include
    private UserId userId;

    private Email email;

    private HashedPassword password;

    @Setter(AccessLevel.PRIVATE)           // Setter privado para control interno
    private Set<RoleName> roles = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    private boolean isActive = true;

    private LocalDateTime createdAt;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastLoginAt;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    // ============================================
    // Factory Methods
    // ============================================

    public static User create(Email email, PlainPassword plainPassword,
                              Set<RoleName> roles, HashedPassword.PasswordEncoder encoder) {
        return new User(
                UserId.generate(),
                email,
                plainPassword.hash(encoder),
                new HashSet<>(roles),
                true,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );
    }

    public static User reconstruct(UserId id, Email email, HashedPassword hashedPassword,
                                   Set<RoleName> roles, boolean isActive,
                                   LocalDateTime createdAt, LocalDateTime lastLoginAt,
                                   LocalDateTime updatedAt) {
        return new User(id, email, hashedPassword, new HashSet<>(roles),
                isActive, createdAt, lastLoginAt, updatedAt);
    }

    // ============================================
    // Domain Methods (Business Logic)
    // ============================================

    public boolean authenticate(PlainPassword plainPassword, HashedPassword.PasswordEncoder encoder) {
        if (!isActive) {
            throw new UserNotActiveException("User is not active");
        }

        boolean matches = this.password.matches(plainPassword.getValue(), encoder);
        if (matches) {
            recordLogin();
        }
        return matches;
    }

    public void changePassword(PlainPassword currentPassword, PlainPassword newPassword,
                               HashedPassword.PasswordEncoder encoder) {
        if (!this.password.matches(currentPassword.getValue(), encoder)) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        this.password = newPassword.hash(encoder);
        this.updatedAt = LocalDateTime.now();
    }

    public void resetPassword(PlainPassword newPassword, HashedPassword.PasswordEncoder encoder) {
        this.password = newPassword.hash(encoder);
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(RoleName role) {
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(RoleName role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasRole(RoleName role) {
        return this.roles.contains(role);
    }

    public boolean hasAnyRole(Set<RoleName> roles) {
        return this.roles.stream().anyMatch(roles::contains);
    }

    public boolean isAdmin() {
        return hasRole(RoleName.admin());
    }

    public boolean isTeacher() {
        return hasRole(RoleName.teacher());
    }

    public boolean isStudent() {
        return hasRole(RoleName.student());
    }

    private void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ============================================
    // Getter con copia defensiva (override del @Getter)
    // ============================================

    public Set<RoleName> getRoles() {
        return new HashSet<>(roles);  // Copia defensiva
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(RoleName::toAuthority)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
         return email.getValue(); // Se usa email en lugar de username
        }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return isActive; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return isActive; }

}