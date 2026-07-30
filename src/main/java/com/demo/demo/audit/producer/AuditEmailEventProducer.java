package com.demo.demo.audit.producer;

import com.demo.demo.audit.event.AuditEmailRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEmailEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String auditEmailTopic;

    public AuditEmailEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${audit.kafka.email-topic}") String auditEmailTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.auditEmailTopic = auditEmailTopic;
    }

    public void publish(AuditEmailRequestedEvent event) {
        kafkaTemplate.send(auditEmailTopic, event.eventId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Unable to publish audit email request {}",
                                event.eventId(), exception);
                        return;
                    }
                    log.info("Published audit email request {} for {}",
                            event.eventId(), event.adminEmail());
                });
    }
}
