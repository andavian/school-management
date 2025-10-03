package org.school.management.auth.infra.web.dto.response;

public record PagedUsersApiResponse(
        java.util.List<UserApiResponse> users,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {}