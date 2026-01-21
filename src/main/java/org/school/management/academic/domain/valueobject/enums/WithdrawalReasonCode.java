package org.school.management.academic.domain.valueobject.enums;

public enum WithdrawalReasonCode {
    SCHOOL_TRANSFER("SCHOOL_TRANSFER", "Cambio de Escuela"),
    MOVE("MOVE", "Mudanza"),
    HEALTH_ISSUES("HEALTH_ISSUES", "Salud"),
    FAMILY_REASON("FAMILY_REASON", "Familiar"),
    ECONOMIC_REASON("ECONOMIC_REASON", "Económico"),
    ABANDONMENT("ABANDONMENT", "Abandono"),
    EXPULSION("EXPULSION", "Expulsión"),
    OTHER("OTHER", "Otro");

    private final String code;
    private final String display;

    WithdrawalReasonCode(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public String getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }
}