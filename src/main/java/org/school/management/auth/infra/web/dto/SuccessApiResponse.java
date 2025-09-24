package org.school.management.auth.infra.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessApiResponse {
    private boolean success;
    private String message;
    private String timestamp;
}
