package org.school.management.storage.domain.model;

/**
 * Value Object que representa el resultado de una subida exitosa.
 *
 * @param objectName   nombre del objeto en el bucket (path relativo — para delete y presigned URLs)
 * @param publicUrl    URL completa del objeto en OCI Object Storage
 * @param fileName     nombre original del archivo
 * @param mimeType     tipo MIME del archivo
 * @param sizeBytes    tamaño en bytes
 */
public record UploadedFile(
        String objectName,
        String publicUrl,
        String fileName,
        String mimeType,
        long sizeBytes
) {
    public UploadedFile {
        if (objectName == null || objectName.isBlank())
            throw new IllegalArgumentException("objectName cannot be blank");
        if (publicUrl == null || publicUrl.isBlank())
            throw new IllegalArgumentException("publicUrl cannot be blank");
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("fileName cannot be blank");
        if (sizeBytes <= 0)
            throw new IllegalArgumentException("sizeBytes must be positive");
    }

    public boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public double getSizeMB() {
        return sizeBytes / (1024.0 * 1024.0);
    }
}