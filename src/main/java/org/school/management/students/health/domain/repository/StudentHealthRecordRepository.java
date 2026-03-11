package org.school.management.students.health.domain.repository;

import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.valueobject.HealthRecordId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.util.Optional;

/**
 * Repository Port — implementado por StudentHealthRecordRepositoryAdapter
 * en infrastructure/persistence/adapter/
 */
public interface StudentHealthRecordRepository {

    Optional<StudentHealthRecord> findByHealthRecordId(HealthRecordId healthRecordId);

    Optional<StudentHealthRecord> findByStudentId(StudentPersonalDataId studentId);

    StudentHealthRecord save(StudentHealthRecord healthRecord);

    boolean existsByStudentId(StudentPersonalDataId studentId);

    void deleteByHealthRecordId(HealthRecordId healthRecordId);
}