package org.school.management.students.records.application.dto.response;

import org.school.management.students.records.domain.valueobject.DocumentCategory;

import java.util.UUID;

public record DocumentTypeResponse(
        UUID documentTypeId,
        String name,
        String code,
        DocumentCategory category,
        boolean mandatory,
        String description,
        Integer validForYears,
        boolean permanent,
        boolean active
) {}