package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
public class BulkUserOperationRequest {
    @NotEmpty(message = "User IDs cannot be empty")
    private Set<String> userIds;

    @NotNull(message = "Operation type is required")
    private OperationType operation;

    // Para operaciones que requieren datos adicionales
    private String newRole;
    private Boolean activeStatus;

    public enum OperationType {
        ACTIVATE,
        DEACTIVATE,
        ADD_ROLE,
        REMOVE_ROLE,
        DELETE
    }
}