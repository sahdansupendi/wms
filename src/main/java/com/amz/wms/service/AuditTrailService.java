package com.amz.wms.service;

import com.amz.wms.dto.audit.AuditTrailResponse;
import com.amz.wms.entity.AuditTrail;
import com.amz.wms.entity.Users;
import com.amz.wms.exception.ResourceNotFoundException;
import com.amz.wms.repository.AuditTrailRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    public AuditTrailService(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Catat aktivitas audit trail secara independen (Propagation.REQUIRES_NEW)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditTrail log(String action,
                          String entityName,
                          String entityId,
                          Object oldValue,
                          Object newValue,
                          String description,
                          Integer httpStatus) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();

            String ipAddress = getClientIp(request);
            String requestMethod = request != null ? request.getMethod() : "SYSTEM";
            String requestUrl = request != null ? request.getRequestURI() : "N/A";

            String userId = extractUserId(request);
            String username = extractUsername(request);

            String oldJson;
            String newJson;

            if (("UPDATE".equalsIgnoreCase(action) || "CHANGE_STATUS".equalsIgnoreCase(action))
                    && oldValue != null && newValue != null) {
                String[] diffs = calculateDiff(oldValue, newValue);
                oldJson = diffs[0];
                newJson = diffs[1];
            } else {
                oldJson = toJsonString(oldValue);
                newJson = toJsonString(newValue);
            }

            AuditTrail auditTrail = AuditTrail.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .timestamp(LocalDateTime.now())
                    .oldValue(oldJson)
                    .newValue(newJson)
                    .ipAddress(ipAddress)
                    .requestMethod(requestMethod)
                    .requestUrl(requestUrl)
                    .respone(httpStatus != null ? httpStatus : 200)
                    .description(description)
                    .build();

            return auditTrailRepository.save(auditTrail);
        } catch (Exception e) {
            log.error("Gagal mencatat audit trail for action [{}], entity [{}]", action, entityName, e);
            return null;
        }
    }

    // Shortcut helpers
    public AuditTrail logCreate(String entityName, String entityId, Object newEntity, String description) {
        return log("CREATE", entityName, entityId, null, newEntity, description, 201);
    }

    public AuditTrail logUpdate(String entityName, String entityId, Object oldEntity, Object newEntity, String description) {
        return log("UPDATE", entityName, entityId, oldEntity, newEntity, description, 200);
    }

    public AuditTrail logDelete(String entityName, String entityId, Object oldEntity, String description) {
        return log("DELETE", entityName, entityId, oldEntity, null, description, 200);
    }

    public AuditTrail logStatusChange(String entityName, String entityId, Object oldEntity, Object newEntity, String description) {
        return log("CHANGE_STATUS", entityName, entityId, oldEntity, newEntity, description, 200);
    }

    public AuditTrail logLogin(String userId, String username, boolean success, String message) {
        HttpServletRequest request = getCurrentHttpRequest();
        String ipAddress = getClientIp(request);
        String requestMethod = request != null ? request.getMethod() : "POST";
        String requestUrl = request != null ? request.getRequestURI() : "/api/auth/login";

        String action = success ? "LOGIN" : "LOGIN_FAILED";
        int status = success ? 200 : 401;

        AuditTrail auditTrail = AuditTrail.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entityName("Auth")
                .entityId(userId != null ? userId : username)
                .timestamp(LocalDateTime.now())
                .oldValue(null)
                .newValue(toJsonString(java.util.Map.of("username", username, "status", success ? "SUCCESS" : "FAILED")))
                .ipAddress(ipAddress)
                .requestMethod(requestMethod)
                .requestUrl(requestUrl)
                .respone(status)
                .description(message)
                .build();

        return auditTrailRepository.save(auditTrail);
    }

    public AuditTrail logLogout(String userId, String username) {
        HttpServletRequest request = getCurrentHttpRequest();
        String ipAddress = getClientIp(request);

        AuditTrail auditTrail = AuditTrail.builder()
                .userId(userId)
                .username(username)
                .action("LOGOUT")
                .entityName("Auth")
                .entityId(userId != null ? userId : username)
                .timestamp(LocalDateTime.now())
                .oldValue(null)
                .newValue(null)
                .ipAddress(ipAddress)
                .requestMethod(request != null ? request.getMethod() : "POST")
                .requestUrl(request != null ? request.getRequestURI() : "/api/auth/logout")
                .respone(200)
                .description("User " + username + " logged out successfully")
                .build();

        return auditTrailRepository.save(auditTrail);
    }

    // Query Methods
    public List<AuditTrailResponse> getAllAuditTrails() {
        return auditTrailRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(AuditTrailResponse::fromEntity)
                .toList();
    }

    public AuditTrailResponse getById(String id) {
        AuditTrail auditTrail = auditTrailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit trail ID " + id + " tidak ditemukan"));
        return AuditTrailResponse.fromEntity(auditTrail);
    }

    public List<AuditTrailResponse> getByUsername(String username) {
        return auditTrailRepository.findByUsernameOrderByTimestampDesc(username)
                .stream()
                .map(AuditTrailResponse::fromEntity)
                .toList();
    }

    public List<AuditTrailResponse> getByEntity(String entityName, String entityId) {
        return auditTrailRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(entityName, entityId)
                .stream()
                .map(AuditTrailResponse::fromEntity)
                .toList();
    }

    public List<AuditTrailResponse> getByAction(String action) {
        return auditTrailRepository.findByActionOrderByTimestampDesc(action)
                .stream()
                .map(AuditTrailResponse::fromEntity)
                .toList();
    }

    // Extractors & Helpers
    private HttpServletRequest getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "UNKNOWN";
    }

    private String extractUserId(HttpServletRequest request) {
        if (request != null && request.getAttribute("userid") != null) {
            return (String) request.getAttribute("userid");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof String) {
            return (String) auth.getDetails();
        }
        return "SYSTEM";
    }

    private String extractUsername(HttpServletRequest request) {
        if (request != null && request.getAttribute("username") != null) {
            return (String) request.getAttribute("username");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    private String toJsonString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        try {
            JsonNode node = objectMapper.valueToTree(obj);
            if (node.isObject() && node.has("password")) {
                ((ObjectNode) node).remove("password");
            }
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private String[] calculateDiff(Object oldObj, Object newObj) {
        if (oldObj == null && newObj == null) {
            return new String[]{null, null};
        }
        if (oldObj == null) {
            return new String[]{null, toJsonString(newObj)};
        }
        if (newObj == null) {
            return new String[]{toJsonString(oldObj), null};
        }

        try {
            JsonNode oldNode = objectMapper.valueToTree(oldObj);
            JsonNode newNode = objectMapper.valueToTree(newObj);

            if (!oldNode.isObject() || !newNode.isObject()) {
                return new String[]{toJsonString(oldObj), toJsonString(newObj)};
            }

            ObjectNode diffOld = objectMapper.createObjectNode();
            ObjectNode diffNew = objectMapper.createObjectNode();

            java.util.Set<String> fieldNames = new java.util.HashSet<>();
            oldNode.fieldNames().forEachRemaining(fieldNames::add);
            newNode.fieldNames().forEachRemaining(fieldNames::add);

            java.util.Set<String> ignoredFields = java.util.Set.of("password", "updatedate", "updateuser");

            for (String fieldName : fieldNames) {
                if (ignoredFields.contains(fieldName)) {
                    continue;
                }

                JsonNode valOld = oldNode.get(fieldName);
                JsonNode valNew = newNode.get(fieldName);

                if (!java.util.Objects.equals(valOld, valNew)) {
                    if (valOld != null) {
                        diffOld.set(fieldName, valOld);
                    }
                    if (valNew != null) {
                        diffNew.set(fieldName, valNew);
                    }
                }
            }

            String oldDiffStr = diffOld.isEmpty() ? "{}" : objectMapper.writeValueAsString(diffOld);
            String newDiffStr = diffNew.isEmpty() ? "{}" : objectMapper.writeValueAsString(diffNew);

            return new String[]{oldDiffStr, newDiffStr};
        } catch (Exception e) {
            log.error("Error calculating audit diff", e);
            return new String[]{toJsonString(oldObj), toJsonString(newObj)};
        }
    }
}
