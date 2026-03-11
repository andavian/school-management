package org.school.management.students.health.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;
import org.school.management.students.health.domain.valueobject.HealthRecordId;
import org.school.management.students.health.infrastructure.persistence.mapper.StudentHealthRecordPersistenceMapper;
import org.school.management.students.health.infrastructure.persistence.repository.StudentHealthRecordJpaRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador: implementa el puerto StudentHealthRecordRepository
 * usando StudentHealthRecordJpaRepository + StudentHealthRecordPersistenceMapper.
 */
@Component
@RequiredArgsConstructor
public class StudentHealthRecordRepositoryAdapter implements StudentHealthRecordRepository {

    private final StudentHealthRecordJpaRepository jpaRepository;
    private final StudentHealthRecordPersistenceMapper mapper;

    @Override
    public Optional<StudentHealthRecord> findByHealthRecordId(HealthRecordId healthRecordId) {
        return jpaRepository.findById(healthRecordId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<StudentHealthRecord> findByStudentId(StudentPersonalDataId studentId) {
        return jpaRepository.findByStudentId(studentId.value())
                .map(mapper::toDomain);
    }

    @Override
    public StudentHealthRecord save(StudentHealthRecord healthRecord) {
        var entity = mapper.toEntity(healthRecord);
        var saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByStudentId(StudentPersonalDataId studentId) {
        return jpaRepository.existsByStudentId(studentId.value());
    }

    @Override
    public void deleteByHealthRecordId(HealthRecordId healthRecordId) {
        jpaRepository.deleteByHealthRecordId(healthRecordId.value());
    }
}