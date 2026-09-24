package com.amz.wms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_trail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "action")
    private String action;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "timestamp", updatable = false)
    @CreationTimestamp
    private LocalDateTime timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_value", columnDefinition = "jsonb")
    private String oldValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_value", columnDefinition = "jsonb")
    private String newValue;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "request_method")
    private String requestMethod;

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "respone")
    private Integer respone;

    @Column(name = "description", length = 1000)
    private String description;
}
