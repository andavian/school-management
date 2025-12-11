package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.*;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.valueobject.RegistryNumber;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.academic.infra.persistence.entity.QualificationRegistryEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface QualificationRegistryMapper {

    @Mapping(target = "registryId", source = "registryId")
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "status", source = "status")
    QualificationRegistryEntity toEntity(QualificationRegistry domain);

    @InheritInverseConfiguration
    QualificationRegistry toDomain(QualificationRegistryEntity entity);

    default UUID mapQualificationRegistryId(RegistryId id) {
        return id != null ? id.getValue() : null;
    }

    default RegistryId mapQualificationRegistryId(UUID uuid) {
        return uuid != null ? new RegistryId(uuid) : null;
    }

    default UUID mapAcademicYearId(AcademicYearId id) {
        return id != null ? id.getValue() : null;
    }

    default AcademicYearId mapAcademicYearId(UUID uuid) {
        return uuid != null ? new AcademicYearId(uuid) : null;
    }

    default String mapStatus(RegistryStatus status) {
        return status != null ? status.name() : null;
    }

    default RegistryStatus mapStatus(String status) {
        return status != null ? RegistryStatus.valueOf(status) : null;
    }

    default String mapRegistryNumber(RegistryNumber number) {
        return number != null ? number.getValue() : null;
    }

    default RegistryNumber mapRegistryNumber(String number) {
        return number != null ? RegistryNumber.of(number) : null;
    }
}
