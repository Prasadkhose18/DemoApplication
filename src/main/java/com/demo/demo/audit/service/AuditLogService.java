package com.demo.demo.audit.service;

import com.demo.demo.audit.entity.AuditLog;
import com.demo.demo.audit.event.TransactionCompletedEvent;
import com.demo.demo.audit.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(TransactionCompletedEvent event) {
        if (auditLogRepository.existsByEventId(event.eventId())) {
            log.info("Ignoring duplicate transaction audit event {}", event.eventId());
            return;
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setEventId(event.eventId());
        auditLog.setReferenceId(event.referenceId());
        auditLog.setTransactionType(event.transactionType());
        auditLog.setAccountNumber(event.accountNumber());
        auditLog.setCounterpartyAccountNumber(event.counterpartyAccountNumber());
        auditLog.setAmount(event.amount());
        auditLog.setBalanceBefore(event.balanceBefore());
        auditLog.setBalanceAfter(event.balanceAfter());
        auditLog.setOccurredAt(event.completedAt());
        auditLog.setRecordedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
        log.info("Stored audit log for transaction reference {}", event.referenceId());
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findLast24Hours() {
        return auditLogRepository
                .findByOccurredAtGreaterThanEqualOrderByOccurredAtDesc(
                        LocalDateTime.now().minusHours(24));
    }
}
