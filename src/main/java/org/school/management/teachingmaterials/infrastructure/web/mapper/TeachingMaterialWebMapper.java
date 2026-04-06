package org.school.management.teachingmaterials.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.teachingmaterials.application.dto.request.UpdateMaterialRequest;
import org.school.management.teachingmaterials.application.dto.request.UploadMaterialRequest;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.infrastructure.web.dto.TeachingMaterialWebDto;

import java.util.List;

/**
 * Web Mapper: Web DTOs ↔ Application DTOs.
 * Nunca toca el dominio directamente — solo cruza capas web ↔ application.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeachingMaterialWebMapper {

    // ── Web request → Application request ────────────────────────────────

    UploadMaterialRequest toUploadRequest(TeachingMaterialWebDto.UploadMaterialWebRequest webRequest);

    UpdateMaterialRequest toUpdateRequest(TeachingMaterialWebDto.UpdateMaterialWebRequest webRequest);

    // ── Application response → Web response ──────────────────────────────

    TeachingMaterialWebDto.TeachingMaterialWebResponse toWebResponse(TeachingMaterialResponse response);

    default TeachingMaterialWebDto.TeachingMaterialListWebResponse toListWebResponse(
            List<TeachingMaterialResponse> responses) {
        List<TeachingMaterialWebDto.TeachingMaterialWebResponse> webList = responses.stream()
                .map(this::toWebResponse)
                .toList();
        return new TeachingMaterialWebDto.TeachingMaterialListWebResponse(webList, webList.size());
    }
}