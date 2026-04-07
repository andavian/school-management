package org.school.management.resources.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;
import org.school.management.resources.infrastructure.persistence.mapper.ResourceUnitPersistenceMapper;
import org.school.management.resources.infrastructure.persistence.repository.ResourceUnitJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResourceUnitRepositoryAdapter implements ResourceUnitRepository {

    private final ResourceUnitJpaRepository jpaRepository;
    private final ResourceUnitPersistenceMapper mapper;

    @Override
    public Optional<ResourceUnit> findByUnitId(UnitId unitId) {
        return jpaRepository.findById(unitId.value()).map(mapper::toDomain);
    }

    @Override
    public List<ResourceUnit> findByResourceIdAndStatus(ResourceId resourceId, UnitStatus status) {
        return jpaRepository.findByResourceIdAndUnitStatus(resourceId.value(), status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUnit> findAllByResourceId(ResourceId resourceId) {
        return jpaRepository.findByResourceId(resourceId.value()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUnitCode(String unitCode) {
        return jpaRepository.existsByUnitCode(unitCode);
    }

    @Override
    public ResourceUnit save(ResourceUnit resourceUnit) {
        var entity = mapper.toEntity(resourceUnit);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}