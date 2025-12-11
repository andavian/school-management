package org.school.management.academic.infra.persistence.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.valueobject.PeriodNumber;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.academic.infra.persistence.entity.EvaluationPeriodEntity;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EvaluationPeriodMapper {


    @Mapping(target = "periodId", source = "periodId")
    @Mapping(target = "academicYearId", source = "academicYearId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EvaluationPeriodEntity toEntity(EvaluationPeriod domain);

    @InheritInverseConfiguration
    EvaluationPeriod toDomain(EvaluationPeriodEntity entity);

    default UUID mapEvaluationPeriodId(PeriodId id) {
        return id != null ? id.getValue() : null;
    }

    default PeriodId mapEvaluationPeriodId(UUID uuid) {
        return uuid != null ? new PeriodId(uuid) : null;
    }

    default UUID mapAcademicYearId(AcademicYearId id) {
        return id != null ? id.getValue() : null;
    }

    default AcademicYearId mapAcademicYearId(UUID uuid) {
        return uuid != null ? new AcademicYearId(uuid) : null;
    }

    default Integer mapPeriodNumber(PeriodNumber number) {
        return number != null ? number.getValue() : null;
    }

    default PeriodNumber mapPeriodNumber(Integer value) {
        return value != null ? PeriodNumber.of(value) : null;
    }
}
