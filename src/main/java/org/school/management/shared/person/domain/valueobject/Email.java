package org.school.management.shared.person.domain.valueobject;

import java.util.regex.Pattern;


public record Email (String value){
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final int MAX_LENGTH = 254;



    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String trimmedValue = value.trim().toLowerCase();

        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email cannot exceed " + MAX_LENGTH + " characters");
        }

        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }

        this.value = trimmedValue;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    public boolean isInstitutional(String institutionalDomain) {
        return getDomain().equalsIgnoreCase(institutionalDomain);
    }
}
