package org.school.management.students.personal.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.students.personal.infrastructure.persistence.mapper.StudentPersonalDataPersistenceMapper;
import org.school.management.students.personal.infrastructure.persistence.repository.StudentPersonalDataJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador del puerto StudentPersonalDataRepository.
 *
 * Convenciones del proyecto:
 * — Nombre: *RepositoryAdapter (módulos nuevos) — no *RepositoryImpl (eso es de auth/).
 * — Recibe VOs del dominio, nunca UUIDs primitivos directamente.
 * — Toda conversión VO ↔ primitivo ocurre aquí antes de llamar al JpaRepository.
 * — Sin lógica de negocio — solo traducción de tipos y delegación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentPersonalDataRepositoryAdapter implements StudentPersonalDataRepository {

    private final StudentPersonalDataJpaRepository jpaRepository;
    private final StudentPersonalDataPersistenceMapper mapper;

    // ── Escritura ─────────────────────────────────────────────────────────

    @Override
    public StudentPersonalData save(StudentPersonalData student) {
        var entity = mapper.toEntity(student);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    // ── Lectura por ID / identificadores únicos ────────────────────────────

    @Override
    public Optional<StudentPersonalData> findByStudentId(StudentPersonalDataId studentId) {
        return jpaRepository.findById(studentId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<StudentPersonalData> findByDni(Dni dni) {
        return jpaRepository.findByDni(dni.value())
                .map(mapper::toDomain);
    }

    // ── Búsquedas (para SearchStudentsUseCase) ─────────────────────────────

    /**
     * Búsqueda por nombre o apellido (parcial, case-insensitive).
     * El puerto recibe el término de búsqueda como String primitivo
     * porque no es un VO del dominio — es un criterio de query.
     */
    @Override
    public List<StudentPersonalData> findByFullNameContaining(String searchTerm) {
        return jpaRepository.searchByFullName(searchTerm)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Filtra estudiantes por localidad de residencia.
     * Extrae el UUID del PlaceId VO antes de pasar al JpaRepository.
     */
    @Override
    public List<StudentPersonalData> findByResidencePlaceId(PlaceId residencePlaceId) {
        return jpaRepository.findByResidencePlaceId(residencePlaceId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<StudentPersonalData> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // ── Verificaciones de unicidad ─────────────────────────────────────────

    @Override
    public boolean existsByDni(Dni dni) {
        return jpaRepository.existsByDni(dni.value());
    }

    /**
     * El puerto recibe el CUIL como String normalizado (11 dígitos sin guiones)
     * — consistente con Cuil.value() del Shared Kernel.
     */
    @Override
    public boolean existsByCuil(String cuil) {
        return jpaRepository.existsByCuil(cuil);
    }

    // ── Conteo ─────────────────────────────────────────────────────────────

    @Override
    public long count() {
        return jpaRepository.count();
    }
}