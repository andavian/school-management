package org.school.management.students.personal.infra.persistence.adapters;

import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.personal.infra.persistence.entity.StudentPersonalDataEntity;
import org.school.management.students.personal.infra.persistence.mapper.StudentPersonalDataMapper;
import org.school.management.students.personal.infra.persistence.repository.StudentPersonalDataJpaRepository;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class StudentPersonalDataRepositoryAdapter implements StudentPersonalDataRepository {

    private final StudentPersonalDataJpaRepository jpaRepository;
    private final StudentPersonalDataMapper mapper;

    public StudentPersonalDataRepositoryAdapter(
            StudentPersonalDataJpaRepository jpaRepository,
            StudentPersonalDataMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<StudentPersonalData> findByStudentId(UUID studentId) {
        return jpaRepository.findById(studentId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<StudentPersonalData> findByDni(Dni dni) {
        return jpaRepository.findByDni(dni.value())
                .map(mapper::toDomain);
    }

    @Override
    public StudentPersonalData save(StudentPersonalData student) {
        StudentPersonalDataEntity entity = mapper.toEntity(student);
        StudentPersonalDataEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByDni(Dni dni) {
        return jpaRepository.existsByDni(dni.value());
    }

    @Override
    public void deleteByStudentId(UUID studentId) {
        jpaRepository.deleteById(studentId);
    }
}