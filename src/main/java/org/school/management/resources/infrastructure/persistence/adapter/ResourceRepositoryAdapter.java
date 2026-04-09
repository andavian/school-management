// src/main/java/org/school/management/resources/infrastructure/persistence/adapter/ResourceRepositoryAdapter.java
package org.school.management.resources.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.school.management.resources.infrastructure.persistence.mapper.ResourcePersistenceMapper;
import org.school.management.resources.infrastructure.persistence.repository.ResourceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResourceRepositoryAdapter implements ResourceRepository {

    private final ResourceJpaRepository jpaRepository;
    private final ResourcePersistenceMapper mapper;

    @Override
    public Optional<Resource> findByResourceId(ResourceId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Resource> findByCode(String code) {
        return jpaRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public List<Resource> findByResourceType(ResourceType type) {
        return jpaRepository.findByResourceType(type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findAllActiveAndReservable() {
        return jpaRepository.findByIsActiveTrueAndIsReservableTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Resource save(Resource resource) {
        var entity = mapper.toEntity(resource);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(ResourceId id) {
        // Soft delete recomendado
        jpaRepository.findById(id.value()).ifPresent(entity -> {
            entity.setActive(false);
            jpaRepository.save(entity);
        });
    }
}