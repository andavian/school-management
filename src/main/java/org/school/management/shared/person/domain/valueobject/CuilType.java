package org.school.management.shared.person.domain.valueobject;

/**
 * Tipos de CUIL según normativa ANSES/AFIP
 */
enum CuilType {
    MALE_ARGENTINEAN("Hombre Argentino"),
    FEMALE_ARGENTINEAN("Mujer Argentino"),
    MALE_FOREIGN("Hombre Extranjero"),
    FEMALE_FOREIGN("Mujer Extranjera"),
    FOREIGN_MERCOSUR("Extranjero Mercosur"),
    LEGAL_ENTITY("Persona Juridica"),
    UNKNOWN("Desconocido");

    private final String displayName;

    CuilType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
