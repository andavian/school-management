package org.school.management.students.records.domain.valueobject;

public enum DocumentTypeCode {
    DNI_FRONT("DNI_FRONT", "DNI Frente"),
    DNI_BACK("DNI_BACK", "DNI Dorso"),
    BIRTH_CERTIFICATE("BIRTH_CERTIFICATE", "Acta de Nacimiento"),
    ADDRESS_CERTIFICATE("ADDRESS_CERTIFICATE", "Certificado de Domicilio"),
    ID_PHOTO("ID_PHOTO", "Foto Carnet"),
    STUDIES_CERTIFICATE("STUDIES_CERTIFICATE", "Certificado de Estudios"),
    SCHOOL_PASS("SCHOOL_PASS", "Pase Escolar"),
    PREVIOUS_REPORT_CARD("PREVIOUS_REPORT_CARD", "Boletín Anterior"),
    HEALTH_CERTIFICATE("HEALTH_CERTIFICATE", "Certificado de Salud"),
    VACCINATION_CARD("VACCINATION_CARD", "Carnet de Vacunación"),
    IMAGE_AUTHORIZATION("IMAGE_AUTHORIZATION", "Autorización de Imagen"),
    OUTING_AUTHORIZATION("OUTING_AUTHORIZATION", "Autorización de Salidas");

    private final String code;
    private final String display;

    DocumentTypeCode(String code, String display) {
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