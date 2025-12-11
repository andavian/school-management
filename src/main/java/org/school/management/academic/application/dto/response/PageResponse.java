package org.school.management.academic.application.dto.response;

public record PageResponse<T> (
        java.util.List<T> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
){

}
