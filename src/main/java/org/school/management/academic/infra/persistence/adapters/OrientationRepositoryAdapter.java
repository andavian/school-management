package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.infra.persistence.entity.OrientationEntity;
import org.school.management.academic.infra.persistence.mappers.OrientationMapper;
import org.school.management.academic.infra.persistence.repository.OrientationJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimización: Transacciones de lectura por defecto
public class OrientationRepositoryAdapter implements OrientationRepository {

    private final OrientationJpaRepository jpaRepository;
    private final OrientationMapper mapper;

    @Override
    @Transactional // Sobreescribe para escritura
    public Orientation save(Orientation orientation) {
        OrientationEntity entity = mapper.toEntity(orientation);
        OrientationEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Orientation> findById(OrientationId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Orientation> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<Orientation> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Orientation> findActiveOrientations() {
        // BUG RESUELTO: Implementa la lógica usando el método JPA
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Orientation> findByAvailableFromYear(int yearLevel) {
        // BUG RESUELTO: Implementa la lógica usando el método JPA
        // NOTA: Usamos findAvailableForYearLevel de JPA ya que es más semántico y completo
        return jpaRepository.findAvailableForYearLevel(yearLevel).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        // BUG RESUELTO: Implementa la lógica usando el método JPA
        return jpaRepository.existsByName(name);
    }

    @Override
    public long count() {
        // BUG RESUELTO: Implementa la lógica usando el método JPA
        return jpaRepository.count();
    }

    @Override
    @Transactional // Sobreescribe para escritura
    public void delete(OrientationId id) {
        jpaRepository.deleteById(id.getValue());
    }
}