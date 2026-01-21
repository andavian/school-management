package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record WithdrawalReasonId(UUID value) {
    public static WithdrawalReasonId generate() {
        return new WithdrawalReasonId(UUID.randomUUID());
    }

    public static WithdrawalReasonId of(String value) {
        return new WithdrawalReasonId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
