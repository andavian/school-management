package org.school.management.storage.domain.service;

import org.school.management.storage.domain.model.UploadedFile;

import java.io.InputStream;

/**
 * Puerto del dominio para almacenamiento de archivos.
 *
 * <p>Sin dependencias de Spring ni OCI — el dominio permanece puro.
 * Implementado por {@code OciObjectStorageService} en {@code infrastructure/}.</p>
 */
public interface StorageService {

    /**
     * Sube un archivo al almacenamiento externo.
     *
     * @param inputStream  contenido del archivo
     * @param fileName     nombre original del archivo
     * @param mimeType     tipo MIME (ej: "application/pdf", "image/jpeg")
     * @param sizeBytes    tamaño en bytes
     * @param folder       carpeta destino dentro del bucket (ej: "records", "materials")
     * @return             {@link UploadedFile} con la URL y metadata del archivo subido
     */
    UploadedFile upload(InputStream inputStream,
                        String fileName,
                        String mimeType,
                        long sizeBytes,
                        String folder);

    /**
     * Elimina un archivo del almacenamiento externo.
     *
     * @param objectName  nombre del objeto en el bucket (path relativo)
     */
    void delete(String objectName);

    /**
     * Genera una URL pre-firmada de acceso temporal (válida {@code expiryMinutes} minutos).
     * Útil para descargas seguras sin exponer credenciales.
     *
     * @param objectName     nombre del objeto en el bucket
     * @param expiryMinutes  minutos de validez de la URL
     * @return               URL pre-firmada
     */
    String generatePresignedUrl(String objectName, int expiryMinutes);
}