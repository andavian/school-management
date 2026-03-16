package org.school.management.teachers.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachers.infrastructure.persistence.entity.TeacherEntity;
import org.school.management.teachers.infrastructure.persistence.mapper.TeacherPersistenceMapper;
import org.school.management.teachers.infrastructure.persistence.repository.TeacherJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeacherRepositoryAdapter implements TeacherRepository {

    private final TeacherJpaRepository jpaRepository;
    private final TeacherPersistenceMapper mapper;

    @Override
    public Optional<Teacher> findByTeacherId(TeacherId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Teacher> findByDni(Dni dni) {
        return jpaRepository.findByDni(dni.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByDni(Dni dni) {
        return jpaRepository.existsByDni(dni.value());
    }

    @Override
    public boolean existsByCuil(String cuil) {
        return jpaRepository.existsByCuil(cuil);
    }

    @Override
    public List<Teacher> findByLastName(String lastName) {
        return jpaRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Teacher save(Teacher teacher) {
        TeacherEntity entity = mapper.toEntity(teacher);
        TeacherEntity saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);


    }

    @Override
    public List<Teacher> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
