// src/main/java/org/school/management/students/enrollment/domain/valueobject/FolioNumber.java
package org.school.management.students.enrollment.domain.valueobject;

import org.school.management.shared.domain.exception.DomainException;

public record FolioNumber(String value) {
    public FolioNumber {
        if (value == null || value.trim().isEmpty())
            throw new NullPointerException("El número de folio es obligatorio");
        value = value.trim().toUpperCase();
        if (!value.matches("^[A-Z0-9-]{1,20}$"))
            throw new IllegalArgumentException("Formato de folio inválido");
    }
}