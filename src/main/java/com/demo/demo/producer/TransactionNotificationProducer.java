package com.demo.demo.producer;

import com.demo.demo.event.TransactionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class TransactionNotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String transactionNotificationTopic;

    public TransactionNotificationProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${transaction.notification.topic}")
            String transactionNotificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionNotificationTopic = transactionNotificationTopic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(TransactionCompletedEvent event) {
        kafkaTemplate.send(transactionNotificationTopic, event.referenceId(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Transaction notification publishing failed. Reference: {}",
                                event.referenceId(), exception);
                        return;
                    }

                    log.info("Transaction notification published. Reference: {}",
                            event.referenceId());
                });
    }
}
