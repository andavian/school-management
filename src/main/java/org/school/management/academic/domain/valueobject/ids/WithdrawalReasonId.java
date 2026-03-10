package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record WithdrawalReasonId(UUID value) {

    public WithdrawalReasonId {
        if (value == null) throw new IllegalArgumentException("WithdrawalReasonId cannot be null");
    }

    public static WithdrawalReasonId of(UUID value) {
        return new WithdrawalReasonId(value);
    }

    public static WithdrawalReasonId generate() {
        return new WithdrawalReasonId(UUID.randomUUID());
    }

    public static WithdrawalReasonId from(UUID uuid) {
        return new WithdrawalReasonId(uuid);
    }

    public static WithdrawalReasonId from(String id) {
        try {
            return new WithdrawalReasonId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid WithdrawalReasonId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}
