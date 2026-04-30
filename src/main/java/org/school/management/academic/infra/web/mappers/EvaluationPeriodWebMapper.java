package org.school.management.academic.infra.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.academic.application.dto.request.CreateEvaluationPeriodRequest;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.infra.web.dto.EvaluationPeriodWebDto.CreateEvaluationPeriodWebRequest;
import org.school.management.academic.infra.web.dto.EvaluationPeriodWebDto.EvaluationPeriodWebResponse;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EvaluationPeriodWebMapper {

    CreateEvaluationPeriodRequest toApplicationRequest(CreateEvaluationPeriodWebRequest webRequest);

    EvaluationPeriodWebResponse toWebResponse(EvaluationPeriodResponse applicationResponse);
}