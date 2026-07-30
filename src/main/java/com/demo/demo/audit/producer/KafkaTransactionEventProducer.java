package com.demo.demo.audit.producer;

import com.demo.demo.audit.event.TransactionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class KafkaTransactionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String transactionTopic;

    public KafkaTransactionEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${audit.kafka.transaction-topic}") String transactionTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionTopic = transactionTopic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(TransactionCompletedEvent event) {
        kafkaTemplate.send(transactionTopic, event.referenceId(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Unable to publish transaction audit event {}",
                                event.eventId(), exception);
                        return;
                    }
                    log.info("Published transaction audit event {} to {}",
                            event.eventId(), transactionTopic);
                });
    }
}
