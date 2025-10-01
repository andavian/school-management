package org.school.management.auth.application.dto.responses;

import java.util.List;

public record PagedUserResponse (
        List<UserResponse> users,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) {

}