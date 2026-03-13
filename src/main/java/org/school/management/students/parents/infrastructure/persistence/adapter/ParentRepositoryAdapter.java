package org.school.management.students.parents.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.shared.person.domain.valueobject.Email;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.parents.infrastructure.persistence.mapper.ParentPersistenceMapper;
import org.school.management.students.parents.infrastructure.persistence.repository.ParentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ParentRepositoryAdapter implements ParentRepository {

    private final ParentJpaRepository jpaRepository;
    private final ParentPersistenceMapper mapper;

    @Override
    public Optional<Parent> findByParentId(ParentId parentId) {
        return jpaRepository.findById(parentId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Parent> findByDni(Dni dni) {
        return jpaRepository.findByDni(dni.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Parent> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByDni(Dni dni) {
        return jpaRepository.existsByDni(dni.value());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public Parent save(Parent parent) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(parent))
        );
    }
}