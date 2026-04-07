package org.school.management.resources.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.school.management.resources.application.dto.request.CreateResourceRequest;
import org.school.management.resources.application.dto.request.UpdateResourceRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.Reservation;

@Mapper(componentModel = "spring")
public interface ResourceApplicationMapper {

    @Mapping(target = "resourceId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Resource toDomain(CreateResourceRequest request);

    void updateDomain(UpdateResourceRequest request, @MappingTarget Resource resource);

    ResourceResponse toResponse(Resource resource);

    ReservationResponse toResponse(Reservation reservation);
}