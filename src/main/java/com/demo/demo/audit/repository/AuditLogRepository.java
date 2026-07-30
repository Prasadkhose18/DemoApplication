package com.demo.demo.audit.repository;

import com.demo.demo.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    boolean existsByEventId(UUID eventId);

    List<AuditLog> findByOccurredAtGreaterThanEqualOrderByOccurredAtDesc(
            LocalDateTime from);
}
