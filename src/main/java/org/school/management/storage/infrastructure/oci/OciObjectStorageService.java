package org.school.management.storage.infrastructure.oci;

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.school.management.storage.domain.model.UploadedFile;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.storage.infrastructure.config.OciStorageProperties;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

/**
 * Implementación del puerto {@link StorageService} para OCI Object Storage.
 *
 * <p>Usa el SDK oficial de Oracle ({@code oci-java-sdk-objectstorage}).
 * El cliente se inicializa una sola vez en {@code @PostConstruct} y se
 * cierra limpiamente en {@code @PreDestroy}.</p>
 *
 * <p>Estructura de carpetas en el bucket:</p>
 * <ul>
 *   <li>{@code records/{studentId}/{uuid}-{fileName}} — documentos del legajo</li>
 *   <li>{@code materials/{teacherId}/{uuid}-{fileName}} — material didáctico</li>
 * </ul>
 */
@Service
@Slf4j
public class OciObjectStorageService implements StorageService {

    private final OciStorageProperties properties;
    private ObjectStorage client;

    public OciObjectStorageService(OciStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() throws Exception {
        log.info("Inicializando OCI Object Storage client — bucket: {}, region: {}",
                properties.getBucketName(), properties.getRegion());

        var authProvider = SimpleAuthenticationDetailsProvider.builder()
                .tenantId(properties.getTenancyOcid())
                .userId(properties.getUserOcid())
                .fingerprint(properties.getFingerprint())
                .privateKeySupplier(() -> {
                    try {
                        return new FileInputStream(properties.getPrivateKeyPath());
                    } catch (Exception e) {
                        throw new IllegalStateException(
                                "No se pudo leer la private key OCI: " + properties.getPrivateKeyPath(), e);
                    }
                })
                .region(com.oracle.bmc.Region.fromRegionId(properties.getRegion()))
                .build();

        this.client = ObjectStorageClient.builder().build(authProvider);
        log.info("OCI Object Storage client inicializado correctamente");
    }

    @PreDestroy
    void destroy() {
        if (client != null) {
            try {
                client.close();
                log.info("OCI Object Storage client cerrado");
            } catch (Exception e) {
                log.warn("Error cerrando OCI client: {}", e.getMessage());
            }
        }
    }

    @Override
    public UploadedFile upload(InputStream inputStream,
                               String fileName,
                               String mimeType,
                               long sizeBytes,
                               String folder) {

        String objectName = buildObjectName(folder, fileName);

        log.info("Subiendo archivo a OCI — bucket: {}, object: {}",
                properties.getBucketName(), objectName);

        var putRequest = PutObjectRequest.builder()
                .namespaceName(properties.getNamespace())
                .bucketName(properties.getBucketName())
                .objectName(objectName)
                .contentLength(sizeBytes)
                .contentType(mimeType)
                .putObjectBody(inputStream)
                .build();

        client.putObject(putRequest);

        String publicUrl = buildObjectUrl(objectName);
        log.info("Archivo subido exitosamente — url: {}", publicUrl);

        return new UploadedFile(objectName, publicUrl, fileName, mimeType, sizeBytes);
    }

    @Override
    public void delete(String objectName) {
        log.info("Eliminando archivo de OCI — object: {}", objectName);

        var deleteRequest = DeleteObjectRequest.builder()
                .namespaceName(properties.getNamespace())
                .bucketName(properties.getBucketName())
                .objectName(objectName)
                .build();

        client.deleteObject(deleteRequest);
        log.info("Archivo eliminado — object: {}", objectName);
    }

    @Override
    public String generatePresignedUrl(String objectName, int expiryMinutes) {
        var expiry = Date.from(
                LocalDateTime.now()
                        .plusMinutes(expiryMinutes)
                        .toInstant(ZoneOffset.UTC)
        );

        var details = CreatePreauthenticatedRequestDetails.builder()
                .name("presigned-" + UUID.randomUUID())
                .objectName(objectName)
                .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead)
                .timeExpires(expiry)
                .build();

        CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(
                CreatePreauthenticatedRequestRequest.builder()
                        .namespaceName(properties.getNamespace())
                        .bucketName(properties.getBucketName())
                        .createPreauthenticatedRequestDetails(details)
                        .build()
        );

        // OCI devuelve la URL completa en accessUri
        String accessUri = response.getPreauthenticatedRequest().getAccessUri();
        String baseUrl = "https://objectstorage."
                + properties.getRegion()
                + ".oraclecloud.com";

        log.debug("URL pre-firmada generada para object: {}, expiry: {} min",
                objectName, expiryMinutes);

        return baseUrl + accessUri;
    }

    // ── helpers ───────────────────────────────────────────────────────────

    /**
     * Genera el nombre del objeto en el bucket con UUID para evitar colisiones.
     * Ejemplo: {@code records/2025-03-15T10:23:45-a3f1b2c4-documento_dni.pdf}
     */
    private String buildObjectName(String folder, String fileName) {
        String safeFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return folder + "/" + UUID.randomUUID() + "-" + safeFileName;
    }

    /**
     * Construye la URL pública del objeto.
     * Formato: https://objectstorage.{region}.oraclecloud.com/n/{namespace}/b/{bucket}/o/{objectName}
     */
    private String buildObjectUrl(String objectName) {
        return "https://objectstorage."
                + properties.getRegion()
                + ".oraclecloud.com/n/"
                + properties.getNamespace()
                + "/b/"
                + properties.getBucketName()
                + "/o/"
                + objectName.replace("/", "%2F");
    }
}