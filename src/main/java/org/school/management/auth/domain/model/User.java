package org.school.management.auth.domain.model;

import lombok.*;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotActiveException;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@ToString(exclude = "password")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User implements UserDetails {

    @EqualsAndHashCode.Include
    private UserId userId;
    private Dni dni;
    private HashedPassword password;

    @Setter(AccessLevel.PRIVATE)
    private Set<Role> roles = new HashSet<>();

    // ✅ CAMPO "active"
    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    private Boolean active = true;

    private LocalDateTime createdAt;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastLoginAt;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    private User(UserId userId, Dni dni, HashedPassword password, Set<Role> roles,
                 boolean active, LocalDateTime createdAt, LocalDateTime lastLoginAt,
                 LocalDateTime updatedAt) {
        this.userId = userId;
        this.dni = dni;
        this.password = password;
        this.roles = new HashSet<>(roles);
        this.active = active;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = updatedAt;
    }

    public static User create(Dni dni, PlainPassword plainPassword,
                              Set<Role> roles, HashedPassword.PasswordEncoder encoder) {
        return new User(
                UserId.generate(),
                dni,
                plainPassword.hash(encoder),
                roles,
                true,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );
    }

    public static User reconstruct(UserId id, Dni dni, HashedPassword hashedPassword,
                                   Set<Role> roles, boolean active,
                                   LocalDateTime createdAt, LocalDateTime lastLoginAt,
                                   LocalDateTime updatedAt) {
        return new User(id, dni, hashedPassword, roles, active, createdAt, lastLoginAt, updatedAt);
    }


    public boolean authenticate(PlainPassword plainPassword, HashedPassword.PasswordEncoder encoder) {
        if (!active) { // Usa "active"
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
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasRole(RoleName roleName) {
        return this.roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    private void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> role.getName().toAuthority())
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return dni.value();
    }

    @Override
    public String getPassword() {
        return password.getValue();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return active; } // Usa "active"

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; } // Usa "active"
}