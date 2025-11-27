package org.school.management.auth.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.persistence.entity.RoleEntity;
import org.school.management.auth.infra.persistence.mappers.AuthPersistenceMapper;
import org.school.management.auth.infra.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final AuthPersistenceMapper mapper;

    @Override
    public Role save(Role role) {
        RoleEntity entity = mapper.roleToRoleEntity(role);
        RoleEntity savedEntity = roleJpaRepository.save(entity);
        return mapper.roleToRoleDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return roleJpaRepository.findById(id.getValue()).map(mapper::roleToRoleDomain);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleJpaRepository.findByName(name.getName()).map(mapper::roleToRoleDomain);
    }

    @Override
    public Set<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(mapper::roleToRoleDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public void delete(RoleId id) {
        roleJpaRepository.deleteById(id.getValue());
    }
}