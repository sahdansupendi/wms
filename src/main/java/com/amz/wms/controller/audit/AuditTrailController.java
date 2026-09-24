package com.amz.wms.controller.audit;

import com.amz.wms.dto.ApiResponse;
import com.amz.wms.dto.audit.AuditTrailResponse;
import com.amz.wms.service.AuditTrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-trail")
public class AuditTrailController {

    private final AuditTrailService auditTrailService;

    public AuditTrailController(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditTrailResponse>>> getAllAuditTrails() {
        List<AuditTrailResponse> list = auditTrailService.getAllAuditTrails();
        return ResponseEntity.ok(ApiResponse.success("Get all audit trails successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditTrailResponse>> getById(@PathVariable("id") String id) {
        AuditTrailResponse response = auditTrailService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<List<AuditTrailResponse>>> getByUsername(@PathVariable("username") String username) {
        List<AuditTrailResponse> list = auditTrailService.getByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Get audit trails for user " + username, list));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    public ResponseEntity<ApiResponse<List<AuditTrailResponse>>> getByEntity(
            @PathVariable("entityName") String entityName,
            @PathVariable("entityId") String entityId) {
        List<AuditTrailResponse> list = auditTrailService.getByEntity(entityName, entityId);
        return ResponseEntity.ok(ApiResponse.success("Get audit trails for entity " + entityName + " ID " + entityId, list));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<List<AuditTrailResponse>>> getByAction(@PathVariable("action") String action) {
        List<AuditTrailResponse> list = auditTrailService.getByAction(action.toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Get audit trails for action " + action, list));
    }
}
