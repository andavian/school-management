package org.school.management.geography.application.dto.request;

public record GlobalSearchRequest(
        String searchTerm,
        Integer maxResults  // Opcional: limitar resultados
) {}
