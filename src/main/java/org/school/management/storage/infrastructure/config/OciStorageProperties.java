package org.school.management.storage.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de OCI Object Storage leídas desde {@code application.yml}.
 *
 * <pre>
 * app:
 *   storage:
 *     oci:
 *       tenancy-ocid: ${OCI_TENANCY_OCID}
 *       user-ocid: ${OCI_USER_OCID}
 *       fingerprint: ${OCI_FINGERPRINT}
 *       private-key-path: ${OCI_PRIVATE_KEY_PATH}
 *       region: ${OCI_REGION}
 *       namespace: ${OCI_NAMESPACE}
 *       bucket-name: ${OCI_BUCKET_NAME}
 *       max-file-size-mb: 10
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "app.storage.oci")
@Getter
@Setter
public class OciStorageProperties {

    /** OCID del tenancy — ej: ocid1.tenancy.oc1..xxx */
    private String tenancyOcid;

    /** OCID del usuario API — ej: ocid1.user.oc1..xxx */
    private String userOcid;

    /** Fingerprint de la API key — ej: aa:bb:cc:dd:... */
    private String fingerprint;

    /** Path al archivo .pem de la private key — ej: /secrets/oci_api_key.pem */
    private String privateKeyPath;

    /** Región OCI — ej: sa-saopaulo-1, us-ashburn-1 */
    private String region;

    /** Namespace del Object Storage (se ve en la consola OCI) */
    private String namespace;

    /** Nombre del bucket — ej: ipet132-documents */
    private String bucketName;

    /** Tamaño máximo permitido por archivo en MB (default: 10) */
    private int maxFileSizeMb = 10;
}