package org.school.management.students.health.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.students.health.application.dto.request.UpdateHealthRecordRequest;
import org.school.management.students.health.application.dto.response.HealthRecordResponse;
import org.school.management.students.health.infrastructure.web.dto.HealthRecordWebDto;

/**
 * Tercera capa de mappers: Web DTOs ↔ Application DTOs.
 * Los campos son idénticos — MapStruct resuelve el mapeo por nombre sin anotaciones adicionales.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HealthRecordWebMapper {

    UpdateHealthRecordRequest toApplicationRequest(HealthRecordWebDto.UpdateHealthRecordWebRequest webRequest);

    HealthRecordWebDto.HealthRecordWebResponse toWebResponse(HealthRecordResponse response);
}