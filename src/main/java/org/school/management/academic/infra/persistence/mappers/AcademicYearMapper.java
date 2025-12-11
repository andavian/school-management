package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.*;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.valueobject.Year;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.infra.persistence.entity.AcademicYearEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR, // Política estricta activada
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AcademicYearMapper {

    // --- DOMAIN -> ENTITY ---
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "status", source = "status")


    // Ignoramos auditoría porque la Entidad lo gestiona con @PrePersist/@PreUpdate
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AcademicYearEntity toEntity(AcademicYear domain);


    // --- ENTITY -> DOMAIN ---
    @InheritInverseConfiguration
    AcademicYear toDomain(AcademicYearEntity entity);


    // --- CUSTOM MAPPERS (Value Objects) ---

    default UUID mapAcademicYearId(AcademicYearId id) {
        return id != null ? id.getValue() : null;
    }

    default AcademicYearId mapAcademicYearId(UUID uuid) {
        return uuid != null ? new AcademicYearId(uuid) : null;
    }

    default Integer mapYear(Year year) {
        return year != null ? year.getValue() : null;
    }

    default Year mapYear(Integer value) {
        return value != null ? Year.of(value) : null;
    }

    // Conversión String <-> Enum (Porque en la entidad Status es String)
    default String mapStatus(AcademicYearStatus status) {
        return status != null ? status.name() : null;
    }

    default AcademicYearStatus mapStatus(String status) {
        return status != null ? AcademicYearStatus.valueOf(status) : null;
    }
}