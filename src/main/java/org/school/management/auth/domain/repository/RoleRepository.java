package org.school.management.auth.domain.repository;

import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(RoleId id);
    Optional<Role> findByName(RoleName name);
    Set<Role> findAll();
    void delete(RoleId id);
}