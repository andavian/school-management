package org.school.management.academic.infra.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.school.management.academic.application.dto.request.CreateQualificationRegistryRequest;
import org.school.management.academic.application.dto.request.InitRegistrySequenceRequest;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.infra.web.dto.QualificationRegistryWebDto.InitRegistrySequenceWebRequest;
import org.school.management.academic.infra.web.dto.QualificationRegistryWebDto.CreateQualificationRegistryWebRequest;
import org.school.management.academic.infra.web.dto.QualificationRegistryWebDto.QualificationRegistryWebResponse;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface QualificationRegistryWebMapper {

    // Web request → Application request
    InitRegistrySequenceRequest toInitRegistrySequenceRequest(InitRegistrySequenceWebRequest webRequest);

    @Mapping(target = "startFolio", ignore = true)
    @Mapping(target = "endFolio", ignore = true)
    @Mapping(target = "maxFolios", ignore = true)
    CreateQualificationRegistryRequest toCreateQualificationRegistryRequest(CreateQualificationRegistryWebRequest webRequest);

    // Application response → Web response
    @Mapping(target = "year", source = "year")          // mapea Integer year (si existe en response)
    @Mapping(target = "isNearlyFull", source = "isNearlyFull")  // boolean primitivo
    QualificationRegistryWebResponse toQualificationRegistryWebResponse(QualificationRegistryResponse response);
}