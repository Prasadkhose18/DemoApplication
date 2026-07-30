package com.demo.demo.audit.consumer;

import com.demo.demo.audit.event.AuditEmailRequestedEvent;
import com.demo.demo.audit.service.AuditEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditEmailConsumer {

    private final ObjectMapper objectMapper;
    private final AuditEmailService auditEmailService;

    public AuditEmailConsumer(ObjectMapper objectMapper,
                              AuditEmailService auditEmailService) {
        this.objectMapper = objectMapper;
        this.auditEmailService = auditEmailService;
    }

    @KafkaListener(topics = "${audit.kafka.email-topic}",
            groupId = "${audit.kafka.audit-email-group-id}")
    public void consume(String payload) {
        try {
            AuditEmailRequestedEvent event = objectMapper.readValue(
                    payload, AuditEmailRequestedEvent.class);
            auditEmailService.sendLast24HourReport(event.adminEmail());
        } catch (Exception exception) {
            log.error("Unable to process audit email request: {}", payload, exception);
            throw new IllegalStateException("Unable to send requested audit email", exception);
        }
    }
}
