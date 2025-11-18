package org.school.management.geography.domain.exception;

public class DuplicatePlaceException extends RuntimeException {
    public DuplicatePlaceException(String name, String provinceName) {
        super("Place already exists: " + name + " in province: " + provinceName);
    }
}
