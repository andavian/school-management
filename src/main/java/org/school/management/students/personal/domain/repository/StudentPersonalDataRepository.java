package org.school.management.students.personal.domain.repository;

import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.students.personal.domain.model.StudentPersonalData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port — implementado por StudentPersonalDataRepositoryAdapter
 * en infrastructure/persistence/adapter/
 */
public interface StudentPersonalDataRepository {

    StudentPersonalData save(StudentPersonalData student);

    Optional<StudentPersonalData> findByStudentId(UUID studentId);

    Optional<StudentPersonalData> findByDni(Dni dni);

    List<StudentPersonalData> findByFullNameContaining(String searchTerm);

    List<StudentPersonalData> findByResidencePlaceId(PlaceId residencePlaceId);

    List<StudentPersonalData> findAll();

    boolean existsByDni(Dni dni);

    boolean existsByCuil(String cuil);

    long count();
}