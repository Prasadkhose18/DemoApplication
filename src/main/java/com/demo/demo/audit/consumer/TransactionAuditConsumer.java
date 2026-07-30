package com.demo.demo.audit.consumer;

import com.demo.demo.audit.event.TransactionCompletedEvent;
import com.demo.demo.audit.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionAuditConsumer {

    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;

    public TransactionAuditConsumer(ObjectMapper objectMapper,
                                    AuditLogService auditLogService) {
        this.objectMapper = objectMapper;
        this.auditLogService = auditLogService;
    }

    @KafkaListener(topics = "${audit.kafka.transaction-topic}",
            groupId = "${audit.kafka.audit-log-group-id}")
    public void consume(String payload) {
        try {
            TransactionCompletedEvent event = objectMapper.readValue(
                    payload, TransactionCompletedEvent.class);
            auditLogService.record(event);
        } catch (Exception exception) {
            log.error("Unable to process transaction audit event: {}", payload, exception);
            throw new IllegalStateException("Unable to store transaction audit event", exception);
        }
    }
}
