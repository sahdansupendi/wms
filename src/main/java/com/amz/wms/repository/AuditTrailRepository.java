package com.amz.wms.repository;

import com.amz.wms.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, String> {

    List<AuditTrail> findAllByOrderByTimestampDesc();

    List<AuditTrail> findByUsernameOrderByTimestampDesc(String username);

    List<AuditTrail> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, String entityId);

    List<AuditTrail> findByActionOrderByTimestampDesc(String action);

    List<AuditTrail> findByEntityNameOrderByTimestampDesc(String entityName);
}
