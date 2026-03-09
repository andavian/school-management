package org.school.management.shared.person.domain.valueobject;

public enum CuilType {
    MALE_ARGENTINEAN("Hombre Argentino"),
    FEMALE_ARGENTINEAN("Mujer Argentina"),
    MALE_FOREIGN("Hombre Extranjero"),
    FEMALE_FOREIGN("Mujer Extranjera"),
    FOREIGN_MERCOSUR("Extranjero Mercosur"),
    LEGAL_ENTITY("Persona Jurídica"),
    UNKNOWN("Desconocido");

    private final String displayName;

    CuilType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}