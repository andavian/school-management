package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BulkOperationResponse {
    private int totalRequested;
    private int successful;
    private int failed;
    private List<String> successfulUserIds;
    private List<OperationError> errors;

    @Data
    @Builder
    public static class OperationError {
        private String userId;
        private String errorMessage;
        private String errorCode;
    }
}