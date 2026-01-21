package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.academic.domain.valueobject.ids.WithdrawalReasonId;

import java.util.Objects;

/**
 * Catálogo: Motivo de Baja
 *
 * Responsabilidad: Representar los motivos por los cuales un estudiante puede darse de baja.
 * No es un agregado, sino un catálogo de dominio.
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class WithdrawalReason {

    @EqualsAndHashCode.Include
    private final WithdrawalReasonId withdrawalReasonId;
    private final String code;
    private final String description;
    private final boolean requiresDocumentation;
    @Builder.Default
    private final boolean isActive = true;



    public boolean canBeUsed() {
        return isActive;
    }

    // Factory method para validación
    public static WithdrawalReason create(WithdrawalReasonBuilder builder) {
        Objects.requireNonNull(builder.withdrawalReasonId, "WithdrawalReasonId cannot be null");

        if (builder.code == null || builder.code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
        if (builder.description == null || builder.description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        if (!builder.code.matches("^[A-Z_]+$")) {
            throw new IllegalArgumentException(
                    "Code must be in UPPERCASE_SNAKE_CASE format: " + builder.code
            );
        }

        return builder.build();
    }
}