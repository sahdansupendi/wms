package com.amz.wms.dto.audit;

import com.amz.wms.entity.AuditTrail;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTrailResponse {

    private String id;
    private String userId;
    private String username;
    private String action;
    private String entityName;
    private String entityId;
    private LocalDateTime timestamp;
    private Object oldValue;
    private Object newValue;
    private String ipAddress;
    private String requestMethod;
    private String requestUrl;
    private Integer respone;
    private String description;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static AuditTrailResponse fromEntity(AuditTrail auditTrail) {
        Object parsedOld = parseJson(auditTrail.getOldValue());
        Object parsedNew = parseJson(auditTrail.getNewValue());

        return AuditTrailResponse.builder()
                .id(auditTrail.getId())
                .userId(auditTrail.getUserId())
                .username(auditTrail.getUsername())
                .action(auditTrail.getAction())
                .entityName(auditTrail.getEntityName())
                .entityId(auditTrail.getEntityId())
                .timestamp(auditTrail.getTimestamp())
                .oldValue(parsedOld)
                .newValue(parsedNew)
                .ipAddress(auditTrail.getIpAddress())
                .requestMethod(auditTrail.getRequestMethod())
                .requestUrl(auditTrail.getRequestUrl())
                .respone(auditTrail.getRespone())
                .description(auditTrail.getDescription())
                .build();
    }

    private static Object parseJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, Object.class);
        } catch (Exception e) {
            return jsonStr;
        }
    }
}
