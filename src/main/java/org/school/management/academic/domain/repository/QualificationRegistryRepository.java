package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;

import java.util.List;
import java.util.Optional;

public interface QualificationRegistryRepository {
    QualificationRegistry save(QualificationRegistry registry);

    Optional<QualificationRegistry> findById(RegistryId registryId);

    Optional<QualificationRegistry> findByRegistryNumber(String registryNumber);

    Optional<QualificationRegistry> findActiveRegistryForYear(AcademicYearId academicYearId);

    List<QualificationRegistry> findByAcademicYear(AcademicYearId academicYearId);

    List<QualificationRegistry> findByStatus(RegistryStatus status);

    List<QualificationRegistry> findAll();

    boolean existsByRegistryNumber(String registryNumber);

    int getAvailableFolios(RegistryId id);

    int incrementFolio(RegistryId id);

    List<QualificationRegistry> findNearFullRegistries(int threshold);

    long countByAcademicYear(AcademicYearId academicYearId);

    long countByStatus(RegistryStatus status);

    void delete(RegistryId id);

    Optional<QualificationRegistry> findActiveInCurrentYear();
}
