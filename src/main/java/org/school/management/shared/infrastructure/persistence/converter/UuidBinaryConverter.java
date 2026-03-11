package org.school.management.shared.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * JPA AttributeConverter para mapear UUID ↔ BINARY(16).
 *
 * Todos los bounded contexts que usen BINARY(16) como PK deben anotar
 * sus campos UUID con @Convert(converter = UuidBinaryConverter.class).
 *
 * El formato usado es BIG-ENDIAN estándar (compatible con MySQL UUID_TO_BIN sin flag swap).
 *
 * Ubicación: shared/infrastructure/persistence/converter/
 * — accesible por todos los bounded contexts de infraestructura.
 */
@Converter
public class UuidBinaryConverter implements AttributeConverter<UUID, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(UUID uuid) {
        if (uuid == null) return null;
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    @Override
    public UUID convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        if (bytes.length != 16) {
            throw new IllegalArgumentException(
                    "Cannot convert BINARY to UUID: expected 16 bytes, got " + bytes.length
            );
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSigBits  = buffer.getLong();
        long leastSigBits = buffer.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }
}